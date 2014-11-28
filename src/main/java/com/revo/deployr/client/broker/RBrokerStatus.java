/*
 * RBrokerStatus.java
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
package com.revo.deployr.client.broker;

/**
 * {@link com.revo.deployr.client.broker.RBroker}
 * status indicating number of currently queued and executing
 * {@link com.revo.deployr.client.broker.RTask}.
 */
public class RBrokerStatus {

    public RBrokerStatus(int pendingTasks,
                         int executingTasks) {

        this.pendingTasks = pendingTasks;
        this.executingTasks = executingTasks;
        this.isIdle = (pendingTasks + executingTasks) == 0;
    }

    /**
     * Number of {@link com.revo.deployr.client.broker.RTask}
     * currently on
     * {@link com.revo.deployr.client.broker.RBroker} queues
     * pending execution.
     */
    public final int pendingTasks;

    /**
     * Number of {@link com.revo.deployr.client.broker.RTask}
     * currently executing on
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public final int executingTasks;

    /**
     * Flag indicating if
     * {@link com.revo.deployr.client.broker.RBroker} is idle.
     * Idle indicates there are currently no pending or executing
     * {@link com.revo.deployr.client.broker.RTask}. An idle
     * {@link com.revo.deployr.client.broker.RBroker} can be
     * safely shutdown.
     */
    public final boolean isIdle;

    public String toString() {
        return "RBrokerStatus: [ " + pendingTasks +
                " ] [ " + executingTasks +
                " ] [ " + isIdle + " ]\n";
    }

}
