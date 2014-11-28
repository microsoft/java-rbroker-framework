/*
 * DiscreteAsynchronous.java
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

import com.revo.deployr.client.broker.RBroker;
import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.RTaskToken;
import com.revo.deployr.client.broker.RTaskResult;
import com.revo.deployr.client.broker.RTaskListener;
import com.revo.deployr.client.broker.config.DiscreteBrokerConfig;
import com.revo.deployr.client.factory.RBrokerFactory;
import com.revo.deployr.client.factory.RTaskFactory;
import org.apache.log4j.Logger;

import static com.revo.deployr.tutorial.util.Constants.*;

/*
 * DiscreteAsynchronous
 *
 * Scenario:
 *
 * RBroker "Hello World" example that demonstrates
 * the basic RBroker programming model:
 * 1. Using RBroker
 * 2. Register RTaskListener for asynchronous
 * notifications on RTask completion events.
 * 3. Execute a single RTask
 * 4. And get notified of RTask completion.
 */
public class DiscreteAsynchronous {

    private static Logger log = Logger.getLogger(DiscreteAsynchronous.class);

    /*
     * Main method launches demo application, DiscreteAsynchronous.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
                System.getProperty("endpoint"));
        new DiscreteAsynchronous();
    }

    public DiscreteAsynchronous() {

        try {

            /*
             * 1. Create RBroker instance using RBrokerFactory.
             *
             * This example creates a DiscreteTaskBroker.
             */

            DiscreteBrokerConfig brokerConfig =
                    new DiscreteBrokerConfig(System.getProperty("endpoint"));
            RBroker rBroker = RBrokerFactory.discreteTaskBroker(brokerConfig);

            /*
             * 2. Register RTaskListener for asynchronous
             * notifications on RTask completion.
             */

            SampleTaskListener myTaskListener =
                    new SampleTaskListener(rBroker);

            rBroker.addTaskListener(myTaskListener);


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


        } catch (Exception tex) {
            log.warn("Runtime exception=" + tex);
        }
    }

    class SampleTaskListener implements RTaskListener {

        public void onTaskCompleted(RTask rTask,
                                    RTaskResult rTaskResult) {
            log.info("onTaskCompleted: " +
                    rTask + ", result: " + rTaskResult);

            if (rBroker != null) {
                rBroker.shutdown();
                log.info("rBroker has been shutdown.");
            }
        }

        public void onTaskError(RTask rTask,
                                Throwable throwable) {
            log.info("onTaskError: " +
                    rTask + ", error: " + throwable);

            if (rBroker != null) {
                rBroker.shutdown();
                log.info("DiscreteAsynchronous: rBroker has been shutdown.");
            }
        }

        public SampleTaskListener(RBroker rBroker) {
            this.rBroker = rBroker;
        }

        private final RBroker rBroker;
    }

}
