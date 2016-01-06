/*
 * DiscretePolling.java
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

import org.apache.log4j.Logger;

/*
 * DiscretePolling
 *
 * Scenario:
 *
 * RBroker "Hello World" example that demonstrates
 * the basic RBroker programming model:
 * 1. Using RBroker
 * 2. To execute a single RTask
 * 3. And poll for the RTask result.
 */
public class DiscretePolling {

    private static Logger log = Logger.getLogger(DiscretePolling.class);

    /*
     * Main method launches demo application, DiscretePolling.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
            System.getProperty("connection.protocol") +
                System.getProperty("connection.endpoint"));
                            
        new DiscretePolling();
    }

    public DiscretePolling() {

        RBroker rBroker = null;

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
            rBroker = RBrokerFactory.discreteTaskBroker(brokerConfig);

            /*
             * 2. Define RTask
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
             * 3. Submit RTask to RBroker for execution.
             *
             * Note, unlike an RClient.executeScript call or
             * an RProject.executeScript call the RBroker.submit
             * call is non-blocking.
             *
             * The RTaskToken is returned immediately. You can
             * use the token to track the progress of RTask
             * and/or block while waiting for a result.
             */

            RTaskToken rTaskToken = rBroker.submit(rTask);

            log.info("Submitted " + rTask +
                    " for execution on RBroker.");

            /*
             * 4. Demonstrate polling for an RTask result.
             */


            while(!rTaskToken.isDone()) {

                log.info("Polling, " +
                    "result not yet available, sleeping, will try again in 1 second.");
                try {
                    Thread.currentThread().sleep(1000);
                } catch(InterruptedException iex) {}
            }

            /*
             * 5. RTaskToken indicates RTask is done. We can now
             * retrieve the result.
             */

            log.info("Polling indicates " +
                                    "result is now available.");

            /*
             * 6. RTask is done, retrieve the RTaskResult.
             *
             * The call to getResult() will either return
             * an RTaskResult or raise an Exception. An Exception
             * indicates the RTask failed to complete and why.
             */

            RTaskResult rTaskResult = rTaskToken.getResult();


            log.info("Returned: " + rTask +
                    " completed, result=" + rTaskResult);

        } catch(Exception tex) {
            log.warn("Runtime exception=" + tex);
        } finally {
            /*
             * Final Step: Shutdown RBroker to release
             * all associated resources, connections.
             */
            if(rBroker != null) {
                rBroker.shutdown();
                log.info("rBroker has been shutdown.");
            }
        }

    }

}
