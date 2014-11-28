/*
 * RBrokerRuntimeStats.java
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
 * Summary {@link com.revo.deployr.client.broker.RTask}
 * statistics for live
 * {@link com.revo.deployr.client.broker.RBroker}.
 * made available on
 * {@link com.revo.deployr.client.broker.RBrokerListener#onRuntimeStats}.
 */
public class RBrokerRuntimeStats {

    /*
     * Total number of {@link com.revo.deployr.client.broker.RTask}
     * run by {@link com.revo.deployr.client.broker.RBroker}.
     */
    public long totalTasksRun = 0L;

    /*
     * Total number of {@link com.revo.deployr.client.broker.RTask}
     * run successfully by
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public long totalTasksRunToSuccess = 0L;

    /*
     * Total number of {@link com.revo.deployr.client.broker.RTask}
     * run that resulted in failure by
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public long totalTasksRunToFailure = 0L;

    /*
     * Total time taken on DeployR to execute the R code on 
     * {@link totalTasksRunToSuccess} successful
     * {@link com.revo.deployr.client.broker.RTask}
     * on 
     * {@link com.revo.deployr.client.broker.RBroker}.
     * <p>
     * The difference between {@link totalTimeTasksOnServer}
     * and {@totalTimeTasksOnCode} gives a good indication
     * of the DeployR server-side overhead processing the execution.
     */
    public long totalTimeTasksOnCode = 0L;

    /*
     * Total time taken on DeployR to process 
     * {@link totalTasksRunToSuccess} successful
     * {@link com.revo.deployr.client.broker.RTask}
     * on 
     * {@link com.revo.deployr.client.broker.RBroker}.
     * <p>
     * The difference between {@link totalTimeTasksOnCall}
     * and {@totalTimeTasksOnServer} gives a good indication
     * of the network latency between your application and the DeployR
     * server.
     */
    public long totalTimeTasksOnServer = 0L;

    /*
     * Total time taken on call to DeployR to process
     * {@link totalTasksRunToSuccess} successful
     * {@link com.revo.deployr.client.broker.RTask}
     * on 
     * {@link com.revo.deployr.client.broker.RBroker}.
     * <p>
     * The difference between {@link totalTimeTasksOnCall}
     * and {@totalTimeTasksOnServer} gives a good indication
     * of the network latency between your application and the DeployR
     * server.
     */
    public long totalTimeTasksOnCall = 0L;

    public String toString() {
        return "\nRBrokerRuntimeStats:\n" +
                "totalTasksRun: " + totalTasksRun +
                "\ntotalTasksRunToSuccess: " + totalTasksRunToSuccess +
                "\ntotalTasksRunToFailure: " + totalTasksRunToFailure +
                "\ntotalTimeTasksOnCode: " + totalTimeTasksOnCode +
                "\ntotalTimeTasksOnServer: " + totalTimeTasksOnServer +
                "\ntotalTimeTasksOnCall: " + totalTimeTasksOnCall +
                "\n";
    }

}
