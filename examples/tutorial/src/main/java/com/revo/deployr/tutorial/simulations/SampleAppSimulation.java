/*
 * SampleAppSimulation.java
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
package com.revo.deployr.tutorial.simulations;

import static com.revo.deployr.tutorial.util.Constants.*;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.task.*;
import com.revo.deployr.client.broker.app.*;
import com.revo.deployr.client.factory.RTaskFactory;
import com.revo.deployr.tutorial.util.RBrokerStatsHelper;

import org.apache.log4j.Logger;

public class SampleAppSimulation implements RTaskAppSimulator,
                                           RTaskListener,
                                           RBrokerListener {

    private static Logger log = Logger.getLogger(SampleAppSimulation.class);

    private final long SIMULATE_TOTAL_TASK_COUNT = 10L;

    private final RBroker rBroker;
    private long simulationStartTime = 0L;

    public SampleAppSimulation(RBroker rBroker) {
        this.rBroker = rBroker;
    }

    /*
     * RTaskAppSimulator method.
     */

    public void simulateApp(RBroker rBroker) {


        /*
         * 1. Prepare RTask(s) for simulation.
         *
         * In the example we will simply simulate the execution
         * a fixed number of RTask.
         *
         * Note, this is a somewhat artificial demo as we are
         * executing the same RTask SIMULATE_TOTAL_TASK_COUNT
         * times. You can experiment by modifying the 
         * implementation to execute a range of RTask of 
         * your own choosing.
         */

        RTask rTask =
            RTaskFactory.discreteTask(TUTORIAL_NOOP_SCRIPT,
                                      TUTORIAL_REPO_DIRECTORY,
                                      TUTORIAL_REPO_OWNER,
                                      null, null);

        /*
         * 2. Loop submitting SIMULATE_TOTAL_TASK_COUNT
         * task(s) to RBroker for execution.
         */

        simulationStartTime = System.currentTimeMillis();

        for(int tasksPushedToBroker = 0;
                tasksPushedToBroker<SIMULATE_TOTAL_TASK_COUNT;
                tasksPushedToBroker++) {

            try {

                RTaskToken taskToken = rBroker.submit(rTask);
                log.info("Submitted task " +
                        rTask + " for execution on RBroker.");

            } catch(Exception ex) {
                log.warn("Runtime exception=" + ex);
            }
        }

    }

    /*
     * RBrokerAsyncListener methods.
     */

    public void onTaskCompleted(RTask rTask, RTaskResult rTaskResult) {
        RBrokerStatsHelper.printRTaskResult(rTask, rTaskResult, null);

    }

    public void onTaskError(RTask rTask, Throwable throwable) {
        RBrokerStatsHelper.printRTaskResult(rTask, null, throwable);
    }

    public void onRuntimeError(Throwable throwable) {
        log.info("onRuntimeError: error: " + throwable + "\n");
    }

    public void onRuntimeStats(RBrokerRuntimeStats stats, int maxConcurrency) {
        RBrokerStatsHelper.printRBrokerStats(stats, maxConcurrency);

        if(stats.totalTasksRun == SIMULATE_TOTAL_TASK_COUNT) {
            log.info("Simulation, total time taken " +
                (System.currentTimeMillis() - simulationStartTime) + " ms.");

            rBroker.shutdown();
            log.info("rBroker has been shutdown.");
        }
    }

}
