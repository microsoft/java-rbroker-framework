/*
 * DiscreteTaskBroker.java
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
import com.revo.deployr.client.broker.config.DiscreteBrokerConfig;
import com.revo.deployr.client.broker.config.RBrokerConfig;
import com.revo.deployr.client.broker.task.DiscreteTask;
import com.revo.deployr.client.broker.worker.DiscreteTaskWorker;
import com.revo.deployr.client.broker.worker.RBrokerWorker;
import com.revo.deployr.client.factory.RClientFactory;

import java.net.URL;

/*
 * DiscreteTaskBroker
 *
 * TBD
 */
public class DiscreteTaskBroker extends RBrokerEngine {

    public DiscreteTaskBroker(DiscreteBrokerConfig brokerConfig)
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
            this.rUser = null;
        }

        /*
         * Prep the base RBrokerEngine.
         */

        initEngine(brokerConfig.maxConcurrentTaskLimit);

        /*
         * Initialize the resourceTokenPool with Integer
         * based resourceTokens.
         */
        for (int i = 0; i < brokerConfig.maxConcurrentTaskLimit; i++) {
            resourceTokenPool.add(new Integer(i));
        }
    }

    public void refresh(RBrokerConfig config) throws RBrokerException {
        throw new RBrokerException("DiscreteTaskBroker configuration " +
                "refresh not supported.");
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

        DiscreteTask discreteTask = (DiscreteTask) task;

        if (rUser == null && discreteTask.external != null) {
            throw new UnsupportedOperationException("External script " +
                    "task execution not permitted on anonymous broker.");
        }

        return super.submit(task, priority);
    }

    public void callback(RTask task, RTaskResult result) {

        Integer resourceToken = (Integer) taskResourceTokenMap.remove(task);

        if (resourceToken != null) {
            boolean added = resourceTokenPool.add(resourceToken);

            if (!added) {
                System.out.println("DiscreteTaskBroker: " +
                        "callback, project could not be added " +
                        "back to pool, ???????.");
            }

        } else {
            System.out.println("DiscreteTaskBroker: " +
                    "callback, task does not have " +
                    "matching project, ???????.");
        }

    }

    protected RBrokerWorker createBrokerWorker(RTask task,
                                               long taskIndex,
                                               boolean isPriorityTask,
                                               Object resourceToken,
                                               RBrokerEngine brokerEngine) {

        return new DiscreteTaskWorker((DiscreteTask) task,
                taskIndex,
                isPriorityTask,
                rClient,
                (Integer) resourceToken,
                (RBroker) brokerEngine);
    }

    protected RTask cloneTask(RTask genesis) {

        DiscreteTask source = (DiscreteTask) genesis;
        DiscreteTask clone = null;
        if (source.external != null) {
            URL externalURL = null;
            try {
                externalURL = new URL(source.external);
            } catch (Exception malex) {
            }
            clone = new DiscreteTask(externalURL,
                    source.options);
        } else {
            clone = new DiscreteTask(source.filename,
                    source.directory,
                    source.author,
                    source.version,
                    source.options);
        }
        clone.setToken(source.getToken());

        return clone;
    }

}
