/*
 * BackgroundTaskBrokerTest.java
 *
 * Copyright (C) 2010-2015 by Revolution Analytics Inc.
 *
 * This program is licensed to you under the terms of Version 2.0 of the
 * Apache License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0) for more details.
 *
 */
package com.revo.deployr.rbroker;

import com.revo.deployr.DeployRUtil;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.config.*;
import com.revo.deployr.client.broker.options.*;
import com.revo.deployr.client.*;
import com.revo.deployr.client.factory.*;
import com.revo.deployr.client.auth.basic.RBasicAuthentication;
import org.junit.*;

import static org.junit.Assert.*;

public class BackgroundTaskBrokerTest {

    String endpoint = null;

    public BackgroundTaskBrokerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        try {
            endpoint = System.getProperty("connection.protocol") +
                        System.getProperty("connection.endpoint");
        } catch (Exception ex) {
            fail("setUp: " + ex);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test "good" endpoint.
     */
    @Test
    public void testEndpointGoodAddress() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        boolean isRejected = true;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        // Test cleanup.
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        assertFalse(isRejected);

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "bad" endpoint.
     */
    @Test
    public void testEndpointBadAddress() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        boolean isRejected = true;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(DeployRUtil.BAD_ENDPOINT, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        // Test cleanup.
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        assertTrue(isRejected);

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test authenticated BackgroundBrokerConfig.
     */
    @Test
    public void testConfigAuthenticationGoodCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobCompleted = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.backgroundTask("testConfigAuthenticationGoodCredentials",
                                                    "Background Task",
                                                    "Histogram of Auto Sales",
                                                    "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                RTaskToken rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobCompleted = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.COMPLETED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        } 

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertNotNull(jobID);
            assertTrue(jobCompleted);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test authenticated BackgroundBrokerConfig using bad credentials.
     */
    @Test
    public void testConfigAuthenticationBadCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        boolean isRejected = true;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     "bad-password-12321");
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        // Test cleanup.
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        assertTrue(isRejected);

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenStandardQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenStandardQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobCompleted = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithTokenStandardQueueGood",
                                                    "Background Task",
                                                    "Histogram of Auto Sales",
                                                     "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobCompleted = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.COMPLETED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
            assertNotNull(jobID);
            assertTrue(jobCompleted);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test BackgroundBrokerConfig RTask
     * priority execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenPriorityQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenPriorityQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobCompleted = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithTokenPriorityQueueGood",
                                                    "Background Task",
                                                    "Histogram of Auto Sales",
                                                     "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask, true);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobCompleted = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.COMPLETED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
            assertNotNull(jobID);
            assertTrue(jobCompleted);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "bad" repository-managed script based
     * RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenBadScript() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenBadScript";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobFailed = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * Repository-managed script does not existe, will
                 * result in an execution error.
                 */
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithTokenBadScript",
                                                    "Background Task",
                                                    "This Script Does Not Exist",
                                                     "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobFailed = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.FAILED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertNotNull(jobID);
            assertTrue(jobFailed);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "bad" code-block based RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenBadCode() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenBadCode";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobFailed = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * R code block will result in a syntax
                 * error being raised by the R interpreter.
                 */
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithTokenBadCode",
                                                    "Background Task",
                                                    "x y", null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobFailed = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.FAILED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertNotNull(jobID);
            assertTrue(jobFailed);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test RTask execution with "good" task execution options.
     */
    @Test
    public void testTaskExecutionWithGoodOptions() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobCompleted = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                BackgroundTaskOptions options =
                    DeployRUtil.createBackgroundTaskOptions(true);
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithGoodOptions",
                                                    "Background Task",
                                                    "Histogram of Auto Sales",
                                                     "root", "testuser", null, options);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobCompleted = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.COMPLETED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertNotNull(jobID);
            assertTrue(jobCompleted);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test RTask execution with "bad" task execution options.
     */
    @Test
    public void testTaskExecutionWithBadOptions() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        BackgroundBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;
        String jobID = null;
        boolean jobFailed = false;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new BackgroundBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.backgroundTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.backgroundTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                BackgroundTaskOptions options =
                    DeployRUtil.createBackgroundTaskOptions(false);
                rTask = RTaskFactory.backgroundTask("testTaskExecutionWithBadOptions",
                                                    "Background Task",
                                                    "Histogram of Auto Sales",
                                                     "root", "testuser", null, options);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.backgroundTask failed: ";
            }
        }

        if(rTask != null) {
            try {
                rTaskToken = rBroker.submit(rTask);
                rTaskResult = rTaskToken.getResult();
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask) failed: ";
            }
        }

        if(rTaskResult != null) {
            jobID = rTaskResult.getID();
        }

        if(jobID != null) {
            jobFailed = DeployRUtil.verifyJobExitStatus(rBroker.owner(),
                                                        jobID,
                                                        RJob.FAILED);
        }

        // Test cleanup.
        try {
            DeployRUtil.deleteJobArtifacts(rBroker.owner(), jobID);
        } catch (Exception dex) {}
        try {
            if (rBroker != null) {
                rBroker.shutdown();
            }
        } catch (Exception ex) {
            cleanupException = ex;
            cleanupExceptionMsg = "rBroker.shutdown failed: ";
        }

        // Test asserts.
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertNotNull(jobID);
            assertTrue(jobFailed);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }
 
}
