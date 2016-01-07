/*
 * BackgroundBasics.java
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
package com.revo.deployr.tutorial.background;

import com.revo.deployr.client.RJob;
import com.revo.deployr.client.RUser;
import com.revo.deployr.client.broker.RBroker;
import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.RTaskToken;
import com.revo.deployr.client.broker.RTaskResult;
import com.revo.deployr.client.broker.RTaskListener;
import com.revo.deployr.client.broker.config.BackgroundBrokerConfig;
import com.revo.deployr.client.auth.*;
import com.revo.deployr.client.auth.basic.*;
import com.revo.deployr.client.factory.RBrokerFactory;
import com.revo.deployr.client.factory.RTaskFactory;
import org.apache.log4j.Logger;

/*
 * BackgroundBasics
 *
 * Scenario:
 *
 * RBroker "Hello World" example that demonstrates
 * the basic background RBroker programming model:
 * 1. Using RBroker
 * 2. Submitting RTask for background execution.
 * 3. Retrieving Job handle from RTaskResult.
 *
 * Important:
 *
 * To handle the results of a Background RTask you
 * must transition from using the RBroker Framework
 * API to using the Java Client Library API. See 
 * example code below for details.
 */
public class BackgroundBasics {

    private static Logger log = Logger.getLogger(BackgroundBasics.class);

    /*
     * Main method launches demo application, BackgroundBasics.
     */
    public static void main(String[] args) {

        log.info("DeployR Endpoint @ " +
            System.getProperty("connection.protocol") +
                System.getProperty("connection.endpoint"));
        new BackgroundBasics();
    }

    public BackgroundBasics() {

        try {

            /*
             * 1. Create RBroker instance using RBrokerFactory.
             *
             * This example creates a BackgroundTaskBroker.
             */

            RAuthentication rAuth =
                    new RBasicAuthentication(System.getProperty("username"),
                            System.getProperty("password"));
            String endpoint = System.getProperty("connection.protocol") +
                                System.getProperty("connection.endpoint");
            boolean allowSelfSigned = 
                Boolean.valueOf(System.getProperty("allow.SelfSignedSSLCert"));

            BackgroundBrokerConfig brokerConfig =
                    new BackgroundBrokerConfig(endpoint, rAuth);
            brokerConfig.allowSelfSignedSSLCert = allowSelfSigned;

            RBroker rBroker =
                    RBrokerFactory.backgroundTaskBroker(brokerConfig);

            /*
             * 2. Register RTaskListener for asynchronous
             * notifications on RTask completion.
             */

            BackgroundTaskListener myTaskListener =
                    new BackgroundTaskListener(rBroker);

            rBroker.addTaskListener(myTaskListener);


            /*
             * 3. Define RTask
             *
             * This example creates a BackgroundTask that will
             * execute an artibrary block of R code.
             */

            String rCode = "x <- rnorm(100)";
            RTask rTask = RTaskFactory.backgroundTask("Example Background RTask",
                    "Example background RTask.",
                    rCode, null);

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

    class BackgroundTaskListener implements RTaskListener {

        public void onTaskCompleted(RTask rTask,
                                    RTaskResult rTaskResult) {

            log.info("onTaskCompleted: " +
                    rTask + ", result: " + rTaskResult);

            /*
             * Retrieve Job identifier from RTaskResult.
             */
            String jobID = rTaskResult.getID();

            log.info("onTaskCompleted: " +
                    rTask + ", background Job ID: " + jobID);

            if (rBroker != null) {

                /*
                 * Important:
                 *
                 * To handle the results of a Background RTask you
                 * must transition from using the RBroker Framework
                 * API to using the Java Client Library API.
                 */
                RUser rUser = rBroker.owner();

                if (rUser != null) {

                    try {

                        RJob rJob = rUser.queryJob(jobID);

                        log.info("onTaskCompleted: " +
                                rTask + ", rJob: " + rJob);

                        /*
                         * Next handle the result of the RJob as appropriate.
                         * In this example, simly cancel and delete the job.
                         */

                        try {
                            rJob.cancel();
                        } catch (Exception cex) {
                            log.warn("rJob.cancel ex=" + cex);
                        }
                        try {
                            rJob.delete();
                        } catch (Exception dex) {
                            log.warn("rJob.delete ex=" + dex);
                        }

                    } catch (Exception jex) {
                        log.warn("rUser.queryJob ex=" + jex);
                    }
                }

                rBroker.shutdown();
                log.info("BackgroundBasics: rBroker has been shutdown.");
            }
        }

        public void onTaskError(RTask rTask,
                                Throwable throwable) {
            log.info("BackgroundBasics: onTaskError: " +
                    rTask + ", error: " + throwable);

            if (rBroker != null) {
                rBroker.shutdown();
                log.info("BackgroundBasics: rBroker has been shutdown.");
            }
        }

        public BackgroundTaskListener(RBroker rBroker) {
            this.rBroker = rBroker;
        }

        private final RBroker rBroker;
    }

}
