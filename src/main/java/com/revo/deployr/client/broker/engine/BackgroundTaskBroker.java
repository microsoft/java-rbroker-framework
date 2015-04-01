/*
 * BackgroundTaskBroker.java
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

import com.revo.deployr.client.RClientException;
import com.revo.deployr.client.RDataException;
import com.revo.deployr.client.RGridException;
import com.revo.deployr.client.RSecurityException;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.config.BackgroundBrokerConfig;
import com.revo.deployr.client.broker.config.RBrokerConfig;
import com.revo.deployr.client.broker.task.BackgroundTask;
import com.revo.deployr.client.broker.worker.BackgroundTaskWorker;
import com.revo.deployr.client.broker.worker.RBrokerWorker;
import com.revo.deployr.client.factory.RClientFactory;

/*
 * BackgroundTaskBroker
 */
public class BackgroundTaskBroker extends RBrokerEngine {

    /*
     * This limit is set simply to ensure the BackgroundTaskBroker
     * does not swamp the server which too many concurrent HTTP 
     * requests when submitting RTask.
     *
     * The real queueing of RTask is handled by the server, this
     * broker simply pushed the RTask into the server-managed queue.
     */
    private static final int PARALLEL_TASK_LIMIT = 10;

    public BackgroundTaskBroker(BackgroundBrokerConfig brokerConfig)
            throws RClientException,
            RSecurityException,
            RDataException,
            RGridException,
            RBrokerException {


        super((RBrokerConfig) brokerConfig);

        this.rClient =
                RClientFactory.createClient(brokerConfig.deployrEndpoint,
                                            brokerConfig.allowSelfSignedSSLCert);

        this.rUser =
                rClient.login(brokerConfig.userCredentials);

        /*
         * Prep the base RBrokerEngine.
         */

        initEngine(PARALLEL_TASK_LIMIT);

        /*
         * Initialize the resourceTokenPool with Integer
         * based resourceTokens.
         */
        for (int i = 0; i < PARALLEL_TASK_LIMIT; i++) {
            resourceTokenPool.add(new Integer(i));
        }
    }

    public void refresh(RBrokerConfig config) throws RBrokerException {
        throw new RBrokerException("BackgroundTaskBroker configuration " +
                " refresh not supported.");
    }

    public final RTaskToken submit(RTask task)
            throws RBrokerException,
            IllegalStateException,
            UnsupportedOperationException {
        return submit(task, false);
    }

    public final RTaskToken submit(RTask task, boolean priority)
            throws RBrokerException,
            IllegalStateException,
            UnsupportedOperationException {

        return super.submit(task, priority);
    }

    public void callback(RTask task, RTaskResult result) {

        Integer resourceToken = (Integer) taskResourceTokenMap.remove(task);

        if (resourceToken != null) {
            boolean added = resourceTokenPool.add(resourceToken);

            if (!added) {
                System.out.println("BackgroundTaskBroker: " +
                        "callback, project could not be added " +
                        "back to pool, ???????.");
            }

        } else {
            System.out.println("BackgroundTaskBroker: " +
                    "callback, task does not have " +
                    "matching project, ???????.");
        }

    }

    protected RBrokerWorker createBrokerWorker(RTask task,
                                               long taskIndex,
                                               boolean isPriorityTask,
                                               Object resourceToken,
                                               RBrokerEngine brokerEngine) {

        return new BackgroundTaskWorker((BackgroundTask) task,
                taskIndex,
                isPriorityTask,
                rUser,
                (Integer) resourceToken,
                (RBroker) brokerEngine);
    }

    protected RTask cloneTask(RTask genesis) {

        BackgroundTask source = (BackgroundTask) genesis;
        BackgroundTask clone = null;
        if (source.code != null) {
            clone = new BackgroundTask(source.name,
                    source.description,
                    source.code,
                    source.options);
        } else {
            clone = new BackgroundTask(source.name,
                    source.description,
                    source.filename,
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

}
