/*
 * PooledTaskBroker.java
 *
 * Copyright (C) 2010-2016, Microsoft Corporation
 *
 * This program is licensed to you under the terms of Version 2.0 of the
 * Apache License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0) for more details.
 *
 */
package com.revo.deployr.client.broker.engine;

import com.revo.deployr.client.*;
import com.revo.deployr.client.broker.RBrokerException;
import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.RTaskResult;
import com.revo.deployr.client.broker.config.PooledBrokerConfig;
import com.revo.deployr.client.broker.config.RBrokerConfig;
import com.revo.deployr.client.broker.impl.RTaskResultImpl;
import com.revo.deployr.client.broker.impl.util.ROptionsTranslator;
import com.revo.deployr.client.broker.task.PooledTask;
import com.revo.deployr.client.broker.worker.PooledTaskWorker;
import com.revo.deployr.client.broker.worker.RBrokerWorker;
import com.revo.deployr.client.factory.RClientFactory;
import com.revo.deployr.client.params.ProjectCreationOptions;
import com.revo.deployr.client.params.ProjectExecutionOptions;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class PooledTaskBroker extends RBrokerEngine {

    public PooledTaskBroker(PooledBrokerConfig brokerConfig)
            throws RClientException,
            RSecurityException,
            RDataException,
            RGridException,
            RBrokerException {

        super((RBrokerConfig) brokerConfig);

        this.rClient =
                RClientFactory.createClient(brokerConfig.deployrEndpoint,
                                            brokerConfig.allowSelfSignedSSLCert);

        if (brokerConfig.userCredentials != null) {
            this.rUser =
                    rClient.login(brokerConfig.userCredentials);
        } else {
            throw new RClientException("Broker failed to initialize, " +
                    "user credentials required.");
        }

        if (brokerConfig.poolCreationOptions != null &&
                brokerConfig.poolCreationOptions.releaseGridResources) {
            this.rUser.releaseProjects();
        }

        ProjectCreationOptions options =
                ROptionsTranslator.translate(brokerConfig.poolCreationOptions);

        List<RProject> deployrProjectPool =
                rUser.createProjectPool(brokerConfig.maxConcurrentTaskLimit,
                        options);

        /*
         * Prep the base RBrokerEngine.
         */

        initEngine(deployrProjectPool.size());

        /*
         * Initialize the resourceTokenPool with RProject.
         */
        for (RProject rProject : deployrProjectPool) {
            resourceTokenPool.add(rProject);
        }

        try {
            brokerEngineExecutor.execute(new HTTPKeepAliveManager(rUser));
        } catch (RejectedExecutionException rex) {
            shutdown();
            throw new RBrokerException("Broker failed " +
                    "to start HTTP keep-alive manager, cause: " + rex);
        }

    }

    public void refresh(RBrokerConfig config) throws RBrokerException {

        if (!status().isIdle) {
            throw new RBrokerException("RBroker is not idle, " +
                    "refresh not permitted.");
        }

        if (!(config instanceof PooledBrokerConfig)) {
            throw new RBrokerException("PooledTaskBroker refresh " +
                    "requires PooledBrokerConfig.");
        }

        PooledBrokerConfig pooledConfig =
                (PooledBrokerConfig) config;

        try {
            /*
             * Temporarily disable RBroker to permit
             * configuration refresh.
             */
            refreshingConfig.set(true);

            ProjectExecutionOptions options =
                    ROptionsTranslator.migrate(pooledConfig.poolCreationOptions);

            for (Object resourceToken : resourceTokenPool) {
                RProject rProject = (RProject) resourceToken;
                /*
                 * Recycle project to remove all existing
                 * workspace objects and directory files.
                 */
                rProject.recycle();
                /*
                 * Execute code to cause workspace and directory
                 * preloads and adoptions to take place.
                 */
                rProject.executeCode("# Refresh project on PooledTaskBroker.", options);
            }

        } catch (Exception rex) {
            throw new RBrokerException("RBroker refresh failed " +
                    " with unexpected error=${rex}");
        } finally {
            /*
             * Re-enabled RBroker following
             * configuration refresh.
             */
            refreshingConfig.set(false);
        }
    }

    public void callback(RTask task, RTaskResult result) {

        RProject rProject = (RProject) taskResourceTokenMap.remove(task);

        /*
         * PooledTaskBroker DeployR Grid Fault Tolerance Handling.
         *
         * An RGridException indicates that an RProject (R session)
         * is no longer reachable due to a slot or node failure on
         * the DeployR grid.
         *
         * An RSecurityException with an error code of 403 indicates
         * that an RProject (R session) is no longer reachable due to an
         * unexpected R session termination on the DeployR grid, perhaps
         * caused by the admin forcibly shutting it down.
         */

        Exception failure = result.getFailure();

        if (failure instanceof RGridException ||
                (failure instanceof RSecurityException &&
                        ((RSecurityException) failure).errorCode == 403)) {

            /*
             * On detection of an RGridException drop the RProject from
             * the pool so further tasks are not directed to that RProject.
             * We achieve this by simply not adding the RProject back to the
             * resourceTokenPool on this callback.
             *
             * We then need to adjust the parallelTaskLimit so the RBroker
             * will report the new (smaller) pool size on
             * RBroker.maxConcurrency() calls.
             */

            if (taskListener != null) {
                /*
                 * When asynchronous listener in use, failed task 
                 * executions due to slot or grid failures can be
                 * automatically resubmitted for execution by the RBroker.
                 *
                 * When RTaskResult.repeatTask is enabled the 
                 * RBrokerEngine.RBrokerListenerManager will skip
                 * calling taskListener.onTaskCompleted(task, result).
                 * This prevents a client application from seeing 
                 * (or having to handle) temporary slot or grid related
                 * failures on RTasks.
                 */
                RTaskResultImpl resultImpl = (RTaskResultImpl) result;
                resultImpl.repeatTask = true;

                /*
                 * Now re-submit for execution using the priority
                 * queue to expedite processing.
                 */

                try {
                    submit(task, true);
                } catch (Exception tex) {
                    System.out.println("PooledTaskBroker: " +
                            "callback, task re-submission ex=" + tex);
                }
            }

            int resizedPoolSize = parallelTaskLimit.decrementAndGet();
            if (brokerListener != null) {
                RBrokerException rbex;
                if (resizedPoolSize == 0) {
                    rbex = new RBrokerException("DeployR grid " +
                            "failure detected, pool no longer operational, " +
                            "advise RBroker shutdown.");
                } else {
                    rbex = new RBrokerException("DeployR grid " +
                            "failure detected, pool size auto-adjusted, max " +
                            " concurrency now " +
                            resizedPoolSize + ".");
                }
                brokerListener.onRuntimeError(rbex);
            }

        } else {

            /*
             * On success or on non-RGridException failures then return
             * the RProject to the pool for use by pending/future tasks.
             */

            if (rProject != null) {
                boolean added = resourceTokenPool.add(rProject);

                if (!added) {
                    System.out.println("PooledTaskBroker: " +
                            "callback, project could not be added " +
                            "back to pool?");
                }

            } else {
                System.out.println("PooledTaskBroker: " +
                        "callback, task does not have " +
                        "matching project?");
            }

        }
    }

    protected RBrokerWorker createBrokerWorker(RTask task,
                                               long taskIndex,
                                               boolean isPriorityTask,
                                               Object resourceToken,
                                               RBrokerEngine brokerEngine) {

        return new PooledTaskWorker((PooledTask) task,
                taskIndex,
                isPriorityTask,
                (RProject) resourceToken,
                brokerEngine);
    }

    protected RTask cloneTask(RTask genesis) {

        PooledTask source = (PooledTask) genesis;
        PooledTask clone = null;
        if (source.code != null) {
            clone = new PooledTask(source.code,
                    source.options);
        } else {
            clone = new PooledTask(source.filename,
                    source.directory,
                    source.author,
                    source.version,
                    source.options);
        }

        if (source.external != null) {
            clone.external = source.external;
        }
        clone.setToken(source.getToken());

        return clone;
    }

    /*
     * HTTPKeepAliveManager
     *
     * Prevents authenticated HTTP session from timing out
     * due to inactivity to ensure pool of RProject remain
     * live and available to PooledTaskBroker.
     */
    private class HTTPKeepAliveManager implements Runnable {

        private final RUser rUser;

        public HTTPKeepAliveManager(RUser rUser) {
            this.rUser = rUser;
        }

        public void run() {

            try {

                while (taskBrokerIsActive.get()) {

                    try {

                        if (rUser != null) {

                            /*
                             * No-Op Ping Authenticated HTTP Session.
                             *
                             * Note, this can throw an exception if
                             * the client has lost it's to server-side
                             * HTTP session on which rUser was auth'd.
                             */
                            rUser.autosaveProjects(false);

                        }

                    } catch (Exception ex) {
                        System.out.println("PooledTaskBroker: " +
                                "HTTPKeepAliveManager ex=" + ex);
                    }

                    try {
                        Thread.currentThread().sleep(PING_INTERVAL);
                    } catch (InterruptedException iex) {
                    }
                }

            } catch (Exception rex) {
                System.out.println("PooledTaskBroker: " +
                        "HTTPKeepAliveManager rex=" + rex);
            }
        }

        private static final long PING_INTERVAL = 60000l;

    }
}
