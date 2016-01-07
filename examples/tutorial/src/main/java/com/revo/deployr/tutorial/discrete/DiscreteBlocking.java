/*
 * DiscreteBlocking.java
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
package com.revo.deployr.tutorial.discrete;

import static com.revo.deployr.tutorial.util.Constants.*;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.config.*;
import com.revo.deployr.client.broker.task.*;
import com.revo.deployr.client.factory.RBrokerFactory;
import com.revo.deployr.client.factory.RTaskFactory;

import org.apache.log4j.Logger;

/*
 * DiscreteBlocking
 *
 * Scenario:
 *
 * RBroker "Hello World" example that demonstrates
 * the basic RBroker programming model:
 * 1. Using RBroker
 * 2. To execute a single RTask
 * 3. And block for the RTask result.
 */
public class DiscreteBlocking {

    private static Logger log = Logger.getLogger(DiscreteBlocking.class);

    RBroker rBroker = null;

    /*
     * Main method launches demo application, DiscreteBlocking.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
            System.getProperty("connection.protocol") +
                System.getProperty("connection.endpoint"));

        new DiscreteBlocking();
    }

    public DiscreteBlocking() {

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
             * 4. Demonstrate blocking for an RTask result.
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
