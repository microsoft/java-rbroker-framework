/*
 * DeployRUtil.java
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
package com.revo.deployr;

import com.revo.deployr.client.*;
import com.revo.deployr.client.about.*;
import com.revo.deployr.client.data.RData;
import com.revo.deployr.client.data.RString;
import com.revo.deployr.client.factory.RDataFactory;
import com.revo.deployr.client.params.*;
import com.revo.deployr.client.broker.options.*;
import org.junit.Ignore;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Ignore
public class DeployRUtil {

    public static final String DEFAULT_PORT = "8000";
    public static final String BAD_ENDPOINT = "http://bad.end.point:999/deployr";
    public static final String BAD_SCRIPT_NAME = "DoesNotExist.R";
    public static final String BAD_DIR_NAME = "not-a-dir";
    public static final String BAD_AUTHOR_NAME = "notauser";

    /*
     * Example-Fraud-Score constants.
     */
    public static final String EFS_DIRECTORY = "example-fraud-score";
    public static final String EFS_BAD_DIRECTORY = "bad-example-fraud-score";
    public static final String EFS_MODEL = "fraudModel.rData";
    public static final String EFS_FUNCTION = "ccFraudScore.R";
    public static final String EFS_USERNAME = "testuser";

    /*
     * createPoolCreationOptions
     *
     * Method returns a fully-populated PoolCreationOptions that
     * can be used to test the set of initialization parameters
     * supported by a PooledBrokerConfig.
     *
     * When the "good" parameter on this method is disabled the
     * configuration returned contains known "bad data" that 
     * can be relied upon to cause pool initialization failures.
     *
     */
    public static PoolCreationOptions createPoolCreationOptions(boolean good) {

        PoolCreationOptions options = new PoolCreationOptions();

        /*
         * Loading all files found in EFS_DIRECTORY.
         */
        options.preloadByDirectory = good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;

        /*
         * Loading EFS_FILE found in EFS_DIRECTORY.
         *
         * Note, setting this option is redundant here in practical
         * terms as the preloadByDirectory would have already loaded
         * the EFS_FUNCTION file from the EFS_DIRECTORY. Howvever,
         * this method is testing the preload directory mechanism.
         */
        options.preloadDirectory = new PoolPreloadOptions();
            options.preloadDirectory.filename = EFS_FUNCTION;
            options.preloadDirectory.directory =
                good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;
            options.preloadDirectory.author = EFS_USERNAME;

        /*
         * Loading EFS_MODEL found in EFS_DIRECTORY.
         */
        options.preloadWorkspace = new PoolPreloadOptions();
            options.preloadWorkspace.filename = EFS_MODEL;
            options.preloadWorkspace.directory =
                good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;
            options.preloadWorkspace.author = EFS_USERNAME;

        /*
         * Loading aribtrary R object.
         */
        options.rinputs = new ArrayList<RData>();
        options.rinputs.add(RDataFactory.createString("arbitrary", "testme"));

        /*
         * Enable automatic grid resource release on pool init/destroy.
         */
        options.releaseGridResources = true;

        return options;
    }

    /*
     * createDiscreteTaskOptions
     *
     * Method returns a fully-populated DiscreteTaskOptions.
     *
     * When the "good" parameter on this method is disabled the
     * configuration returned contains known "bad data" that 
     * can be relied upon to cause task initialization failures.
     *
     */
    public static DiscreteTaskOptions createDiscreteTaskOptions(boolean good) {

        DiscreteTaskOptions options = new DiscreteTaskOptions();

        return (DiscreteTaskOptions) populateTaskOptions(options, good);
    }

    /*
     * createPooledTaskOptions
     *
     * Method returns a fully-populated PooledTaskOptions.
     *
     * When the "good" parameter on this method is disabled the
     * configuration returned contains known "bad data" that 
     * can be relied upon to cause task initialization failures.
     *
     */
    public static PooledTaskOptions createPooledTaskOptions(boolean good) {

        PooledTaskOptions options = new PooledTaskOptions();

        return (PooledTaskOptions) populateTaskOptions(options, good);
    }

    /*
     * createBackgroundTaskOptions
     *
     * Method returns a fully-populated BackgroundTaskOptions.
     *
     * When the "good" parameter on this method is disabled the
     * configuration returned contains known "bad data" that 
     * can be relied upon to cause task initialization failures.
     *
     */
    public static BackgroundTaskOptions createBackgroundTaskOptions(boolean good) {

        BackgroundTaskOptions options = new BackgroundTaskOptions();

        return (BackgroundTaskOptions) populateTaskOptions(options, good);
    }

    private static TaskOptions populateTaskOptions(TaskOptions options,
                                                   boolean good) {

        /*
         * Loading all files found in EFS_DIRECTORY.
         */
        options.preloadByDirectory = good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;

        /*
         * Loading EFS_FILE found in EFS_DIRECTORY.
         */
        options.preloadDirectory = new TaskPreloadOptions();
            options.preloadDirectory.filename = EFS_FUNCTION;
            options.preloadDirectory.directory =
                good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;
            options.preloadDirectory.author = EFS_USERNAME;

        /*
         * Loading EFS_MODEL found in EFS_DIRECTORY.
         */
        options.preloadWorkspace = new TaskPreloadOptions();
            options.preloadWorkspace.filename = EFS_MODEL;
            options.preloadWorkspace.directory =
                good ? EFS_DIRECTORY : EFS_BAD_DIRECTORY;
            options.preloadWorkspace.author = EFS_USERNAME;

        /*
         * Loading aribtrary R object.
         */
        options.rinputs = new ArrayList<RData>();
        options.rinputs.add(RDataFactory.createString("arbitrary", "testme"));

        return options;
    }

    /*
     * verifyJobExitStatus
     *
     * Test and verify that a Job has reached a given "status".
     * This implementation will busy-wait for up to 2 minutes
     * for the Job to complete, otherwise it returns failure.
     */
    public static boolean verifyJobExitStatus(RUser rUser,
                                              String jobID,
                                              String status) {

        boolean verified = false;

        try {

            RJob rJob = rUser.queryJob(jobID);

            if(rJob != null) {

                // wait for job to complete
                int t = 120;  // Try for up to 2 minutes.
                while (t-- != 0) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                    try {

                         RJobDetails rJobDetails = rJob.query();

                        if (rJobDetails.status.equalsIgnoreCase(status)) {
                                /*
                                 * RJobDetails.status matches status
                                 * to be verified, break.
                                 */
                                verified = true;
                                break;
                        } else
                        if (JOB_DONE_STATES.contains(rJobDetails.status)) {
                                /*
                                 * JobDetails.status matches a "done"
                                 * state that is not the state to be
                                 * verified, so break.
                                 */
                                break;
                        }

                    } catch (Exception ex) {
                        break;
                    }
                }
            }

        } catch(Exception ex) {

        }

        return verified;
    }

    public static List<String> JOB_DONE_STATES = Arrays.asList(RJob.COMPLETED,
                                                               RJob.CANCELLED,
                                                               RJob.INTERRUPTED,
                                                               RJob.FAILED,
                                                               RJob.ABORTED);
    /*
     * deleteJobArtifacts
     *
     * Following a unit test all artifacts assocated with an
     * RJob should be removed from the DeployR database. This
     * implementation removes the RJob and the associated 
     * RProject if one was generated as a result of the RJob.
     *
     * Note, this method will not throw any Exceptions.
     */
    public static void deleteJobArtifacts(RUser rUser, String jobID) {

       try {

            RJob rJob = rUser.queryJob(jobID);

            if(rJob != null) {

                try {
                    /*
                     * Delete RJob generated RProject.
                     */
                    String projectID = rJob.about().project;
                    if(projectID != null) {
                        RProject rProject = rUser.getProject(projectID);
                        if(rProject != null) {
                            rProject.delete();
                        }
                    }
                } catch(Exception pex) {}

               try {
                    /*
                     * Ensure RJob is stopped.
                     */
                    rJob.cancel();
                } catch(Exception cex) {}
                try {
                    /*
                     * Delete RJob.
                     */
                    rJob.delete();
                } catch(Exception dex) {}

            }

        } catch(Exception ex) {}
    }

}
