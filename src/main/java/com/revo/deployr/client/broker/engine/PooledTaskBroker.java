/*
 * PooledTaskBroker.java
 *
 * Copyright (C) 2010-2014 by Revolution Analytics Inc.
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
                RClientFactory.createClient(brokerConfig.deployrEndpoint);

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

        RProject rProject = (RProject) taskResourceTokenMap.get(task);

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
