/*
 * ScoringEngineSimulation.java
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
package com.revo.deployr.tutorial.simulations;

import static com.revo.deployr.tutorial.util.Constants.*;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.task.*;
import com.revo.deployr.client.broker.options.*;
import com.revo.deployr.client.broker.app.*;
import com.revo.deployr.client.data.*;
import com.revo.deployr.client.factory.RDataFactory;
import com.revo.deployr.client.factory.RTaskFactory;

import com.revo.deployr.tutorial.util.RBrokerStatsHelper;

import java.util.*;
import org.apache.log4j.Logger;

/*
 * This ScoringEngineSimulation is a simple extension
 * to the SampleAppSimulation class used by PooledSimulation.
 *
 * The addition of the SIMULATE_TASK_RATE_PER_MINUTE parameter
 * allows the app simulation to mimick more "real world"
 * workloads by adjusting the rate at which RTask are 
 * submitted to the RBroker for execution.
 *
 * Note, this simulation also demonstrates how input values
 * can be passed to each RTask and how output values can
 * be retrieved following the execution of each task.
 *
 * In this example, a "customerid" input value is passed on
 * each RTask. While this example passes a simple index
 * value for "customerid" a "real world" app would like pass
 * a database record id along with additional customer specific
 * data parameters.
 *
 * In this example, the value of the "score" is returned as a
 * DeployR-encoded R object and made available on the result
 * for the RTask.
 */
public class ScoringEngineSimulation implements RTaskListener,
                                                RBrokerListener,
                                                RTaskAppSimulator {

    private static Logger log = Logger.getLogger(ScoringEngineSimulation.class);


    private long SIMULATE_TOTAL_TASK_COUNT = 50;
    private long SIMULATE_TASK_RATE_PER_MINUTE = 500L;  

    private final RBroker rBroker;
    public long simulationStartTime = 0L;

    public ScoringEngineSimulation(RBroker rBroker) {
        this.rBroker = rBroker;
    }

    /*
     * RTaskAppSimulator method.
     */

    public void simulateApp(RBroker rBroker) {

        /*
         * 2. Submit task(s) to RBroker for execution.
         */

        log.info("About to simulate " +
                SIMULATE_TOTAL_TASK_COUNT + " tasks at a rate of " +
                SIMULATE_TASK_RATE_PER_MINUTE + " tasks per minutes.");

        simulationStartTime = System.currentTimeMillis();

        for(int tasksPushedToBroker = 0;
                tasksPushedToBroker<SIMULATE_TOTAL_TASK_COUNT;
                tasksPushedToBroker++) {

            try {

                /*
                 * 1. Prepare RTask for real-time scoring.
                 *
                 * In this example, we pass along a unique
                 * customer ID with each RTask. In a real-world
                 * application the input parameters on each RTask
                 * will vary depending on need, such as customer
                 * database record keys and supplimentary parameter
                 * data to facilitate the scoring.
                 */

                PooledTaskOptions taskOptions =
                                    new PooledTaskOptions();
                taskOptions.routputs = Arrays.asList("score");
                taskOptions.rinputs = Arrays.asList((RData)
                   RDataFactory.createNumeric("customerid", tasksPushedToBroker));

                RTask rTask =
                    RTaskFactory.pooledTask(TUTORIAL_RTSCORE_SCRIPT,
                                            TUTORIAL_REPO_DIRECTORY,
                                            TUTORIAL_REPO_OWNER,
                                            null, taskOptions);

                RTaskToken taskToken = rBroker.submit(rTask);
                log.info("Submitted task " +
                        rTask + "\n");

                /*
                 * If further tasks need to be pushed to broker
                 * then delay for staggeredLoadInterval to simulate
                 * control of task flow rate.
                 */
                if(tasksPushedToBroker <
                        (SIMULATE_TOTAL_TASK_COUNT - 1)) {


                    try {

                        if(SIMULATE_TASK_RATE_PER_MINUTE != 0L) {

                            long staggerLoadInterval = 
                                (long)((float)60/SIMULATE_TASK_RATE_PER_MINUTE*1000);
                            Thread.currentThread().sleep(
                                            staggerLoadInterval);
                        }

                    } catch(InterruptedException iex) {}
                }

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
        log.info("onTaskCompleted: " + rTask + ", score " +
            ((RNumeric)rTaskResult.getGeneratedObjects().get(0)).getValue());
    }

    public void onTaskError(RTask rTask, Throwable throwable) {
        RBrokerStatsHelper.printRTaskResult(rTask, null, throwable);
    }

    public void onRuntimeError(Throwable throwable) {
        log.info("onRuntimeError: error: " + throwable);
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
