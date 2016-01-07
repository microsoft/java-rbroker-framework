/*
 * DiscreteSimulation.java
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

import com.revo.deployr.client.broker.RBroker;
import com.revo.deployr.client.broker.config.DiscreteBrokerConfig;
import com.revo.deployr.client.factory.RBrokerFactory;
import com.revo.deployr.tutorial.simulations.SampleAppSimulation;
import org.apache.log4j.Logger;

/*
 * DiscreteSimulation
 *
 * So far these examples have demonstrated how to execute 
 * and handle the result of a single RTask.
 *
 * How do we scale up using the RBroker programming model?
 *
 * We need to solve to two technical challenges:
 *
 * 1. How do we build a client application to test DeployR scale?
 * 1. How does a client application handle RTask results at scale?
 *
 * This example demonstrates how RTaskAppSimulator can be
 * used to simulate arbitrarily large RTask throughput for
 * task-testing or load-testing.
 *
 * Using an RTaskListener demonstrates how to handle
 * RTask results at scale.
 *
 * Steps:
 *
 * 1. Using RBroker
 * 2. Register an RTaskAppSimulator, an RTaskListener
 * and an RBrokerListener.
 * 3. To simulate the execution of many RTask
 * 4. Retrieve RTask results on asynchronous callback.
 * 5. And observe RBroker built-in profiling capabilities.
 */
public class DiscreteSimulation {

    private static Logger log = Logger.getLogger(DiscreteSimulation.class);

    /*
     * Main method launches demo application, DiscreteSimulation.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
            System.getProperty("connection.protocol") +
                System.getProperty("connection.endpoint"));
        new DiscreteSimulation();
    }

    public DiscreteSimulation() {

        try {

            /*
             * 1. Create RBroker instance using RBrokerFactory.
             *
             * This example creates an anonymous DiscreteTaskBroker.
             */

            String endpoint = System.getProperty("connection.protocol") +
                                System.getProperty("connection.endpoint");
            boolean allowSelfSigned = 
                Boolean.valueOf(System.getProperty("allow.SelfSignedSSLCert"));
            DiscreteBrokerConfig brokerConfig =
                    new DiscreteBrokerConfig(endpoint, null, 5);
            brokerConfig.allowSelfSignedSSLCert = allowSelfSigned;
            RBroker rBroker = RBrokerFactory.discreteTaskBroker(brokerConfig);

            /*
             * 2. Create an instance of RTaskAppSimulator. It will drive
             * RTasks through the RBroker.
             */

            SampleAppSimulation simulation =
                    new SampleAppSimulation(rBroker);


            /*
             * 3. Launch RTaskAppSimulator simulation.
             */
            rBroker.simulateApp(simulation);


        } catch (Exception tex) {
            log.warn("constructor: ex=" + tex);
        }

    }
}
