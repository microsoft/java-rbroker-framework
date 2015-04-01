/*
 * DiscreteProfiling.java
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
package com.revo.deployr.tutorial.discrete;

import static com.revo.deployr.tutorial.util.Constants.*;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.config.*;
import com.revo.deployr.client.broker.task.*;
import com.revo.deployr.client.factory.RBrokerFactory;
import com.revo.deployr.client.factory.RTaskFactory;

import com.revo.deployr.tutorial.util.RBrokerStatsHelper;

import org.apache.log4j.Logger;

/*
 * DiscreteProfiling
 *
 * Scenario:
 *
 * RBroker "Hello World" example that demonstrates
 * runtime statistics
 * 1. Using RBroker
 * 2. Register RTaskListener for asynchronous
 * notifications on RTask completion events.
 * 2. Register RBrokerListener for asynchronous
 * notifications on RBroker statistic events.
 * 3. Execute a single RTask.
 * 4. And get notified of RTask completion plus
 * RBroker statistics events.
 */
public class DiscreteProfiling {

    private static Logger log = Logger.getLogger(DiscreteProfiling.class);

    /*
     * Main method launches demo application, DiscreteProfiling.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
            System.getProperty("connection.protocol") +
                System.getProperty("connection.endpoint"));
        new DiscreteProfiling();
    }

    public DiscreteProfiling() {

        try {

            /*
             * 1. Create RBroker instance using RBrokerFactory.
             *
             * This example creates a DiscreteTaskBroker.
             */
            String endpoint = System.getProperty("connection.protocol") +
                                System.getProperty("connection.endpoint");
            boolean allowSelfSigned = 
                Boolean.valueOf(System.getProperty("allow.SelfSignedSSLCert"));
            DiscreteBrokerConfig brokerConfig =
                    new DiscreteBrokerConfig(endpoint);
            brokerConfig.allowSelfSignedSSLCert = allowSelfSigned;
            RBroker rBroker = RBrokerFactory.discreteTaskBroker(brokerConfig);

            /*
             * 2. Register RTaskListener for asynchronous
             * notifications on RTask completion.
             */

            SampleTaskBrokerListener sampleListeners =
                                    new SampleTaskBrokerListener(rBroker);

            rBroker.addTaskListener(sampleListeners);
            rBroker.addBrokerListener(sampleListeners);


            /*
             * 3. Define RTask
             *
             * This example creates a DiscreteTask that will
             * execute an R script:
             * /testuser/tutorial-rbroker/5SecondNoOp.
             */

            RTask rTask =
                RTaskFactory.discreteTask(TUTORIAL_NOOP_SCRIPT,
                                          TUTORIAL_REPO_DIRECTORY,
                                          TUTORIAL_REPO_OWNER,
                                          null, null);
            /*
             * 4. Submit RTask to RBroker for execution.
             *
             * The RTaskToken is returned immediately. You can
             * use the token to track the progress of RTask
             * and/or block while waiting for a result.
             *
             * However, in this example we are going to allow
             * the RTaskListener handle the result so 
             * there is nothing further for us to do here after
             * we submit the RTask.
             */

            RTaskToken rTaskToken = rBroker.submit(rTask);

            log.info("Submitted " + rTask +
                    " for execution on RBroker.");


        } catch(Exception tex) {
            log.warn("Runtime exception=" + tex);
        }
    }

   class SampleTaskBrokerListener implements RTaskListener,
                                             RBrokerListener {

        public void onTaskCompleted(RTask rTask, RTaskResult rTaskResult) {
            log.info("onTaskCompleted: " +
                        rTask + ", result: " + rTaskResult);
            RBrokerStatsHelper.printRTaskResult(rTask, rTaskResult, null);
        }

        public void onTaskError(RTask rTask, Throwable throwable) {
            log.info("onTaskError: " +
                        rTask + ", error: " + throwable);
            RBrokerStatsHelper.printRTaskResult(rTask, null, throwable);
        }

        public void onRuntimeError(Throwable throwable) {
            log.info("onRuntimeError: error: " + throwable + "\n");

            if(rBroker != null) {
                rBroker.shutdown();
                log.info("DiscreteProfiling: rBroker has been shutdown.");
            }
        }

        public void onRuntimeStats(RBrokerRuntimeStats stats,
                                        int maxConcurrency) {

            RBrokerStatsHelper.printRBrokerStats(stats,
                                        maxConcurrency);

            if(rBroker != null) {
                rBroker.shutdown();
                log.info("rBroker has been shutdown.");
            }
        }

        public SampleTaskBrokerListener(RBroker rBroker) {
            this.rBroker = rBroker;
        }

        public final RBroker rBroker;
    }

}
