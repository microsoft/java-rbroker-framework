/*
 * DiscreteTaskBrokerTest.java
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
import java.util.*;

import static org.junit.Assert.*;

public class DiscreteTaskBrokerTest {

    String endpoint = null;

    public DiscreteTaskBrokerTest() {
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
        DiscreteBrokerConfig config = null;
        boolean isRejected = true;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
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
        DiscreteBrokerConfig config = null;
        boolean isRejected = true;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(DeployRUtil.BAD_ENDPOINT);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
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
     * Test anonymous DiscreteBrokerConfig.
     */
    @Test
    public void testConfigAnonymousNoCredentials() {

        // Test variables.
        RBroker rBroker = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {

            try {
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test authenticated DiscreteBrokerConfig.
     */
    @Test
    public void testConfigAuthenticationGoodCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new DiscreteBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test authenticated DiscreteBrokerConfig using bad credentials.
     */
    @Test
    public void testConfigAuthenticationBadCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
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
        config = new DiscreteBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
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
        assertTrue(exception instanceof RClientException);

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

     /**
     * Test DiscreteBrokerConfig using non-default max concurrency.
     */
    @Test
    public void testConfigCustomConcurrencyLimit() {

        // Test variables.
        int MAX_CONCURRENCY = 10;
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new DiscreteBrokerConfig(endpoint, rAuth, MAX_CONCURRENCY);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "good" RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenStandardQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenStandardQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "good" RTask priority execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenPriorityQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenPriorityQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test multiple "good" RTask executions distributed across
     * the standard and priority task queues.
     */
    @Test
    public void testMultipleTaskExecutionWithMixedPriority() {

        // Test variables.
        String executionToken = "testMultipleTaskExecutionWithMixedPriority";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        List<RTask> rTasks = new ArrayList<RTask>();
        List<RTaskToken> standardTaskTokens = new ArrayList<RTaskToken>();
        List<RTaskToken> priorityTaskTokens = new ArrayList<RTaskToken>();
        List<RTaskResult> rTaskResults = new ArrayList<RTaskResult>();

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        int multipleTaskMaxConcurrency = 10;
        int multipleTaskTestSize = 10;
        config = new DiscreteBrokerConfig(endpoint, null, multipleTaskMaxConcurrency);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {

            for(int i=0; i<multipleTaskTestSize; i++) {
                try {
                    RTask rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                             "root", "testuser", null, null);
                    rTasks.add(rTask);
                } catch (Exception ex) {
                    exception = ex;
                    exceptionMsg = "RTaskFactory.discreteTask failed: ";
                    break;
                }
            }
        }

        boolean priorityTask = true;
        for(RTask task : rTasks) {
            try {
                RTaskToken rTaskToken = rBroker.submit(task, priorityTask);
                if(priorityTask)
                    priorityTaskTokens.add(rTaskToken);
                else
                    standardTaskTokens.add(rTaskToken);

                // Flip priority flag to altenate task type.
                priorityTask = !priorityTask;
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "rBroker.submit(rTask, priorityTask) failed: ";
            }
        }

        for(RTaskToken taskToken : priorityTaskTokens) {
            try {
                rTaskResults.add(taskToken.getResult());
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "Priority rTaskToken.getResult() failed: ";
            }
        }

        for(RTaskToken taskToken : standardTaskTokens) {
            try {
                rTaskResults.add(taskToken.getResult());
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "Standard rTaskToken.getResult() failed: ";
            }
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
        if (exception == null) {
            assertEquals(rTasks.size(), multipleTaskTestSize);
            assertEquals(standardTaskTokens.size() + priorityTaskTokens.size(),
                                                        multipleTaskTestSize);
            assertEquals(rTaskResults.size(), multipleTaskTestSize);
            for(RTaskResult result : rTaskResults) {
                assertTrue(result.isSuccess());
            }
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test "bad" RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenBadScript() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenBadScript";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.discreteTask(DeployRUtil.BAD_SCRIPT_NAME,
                                                  DeployRUtil.BAD_DIR_NAME,
                                                  DeployRUtil.BAD_AUTHOR_NAME,
                                                  null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertFalse(rTaskResult.isSuccess());
            assertNotNull(rTaskResult.getFailure());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }

    /**
     * Test RTask execution with good task execution options.
     */
    @Test
    public void testTaskExecutionWithGoodOptions() {

        // Test variables.
        String executionToken = "testTaskExecutionWithToken";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new DiscreteBrokerConfig(endpoint, rAuth);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * createDiscreteTaskOptions(true) returns "good" config.
                 *
                 * Note, "good" config uses repository-managed files
                 * that require use of an authenticated RBroker instance,
                 * hence use of rAuth above.
                 */ 
                DiscreteTaskOptions options =
                    DeployRUtil.createDiscreteTaskOptions(true);
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, options);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertTrue(rTaskResult.isSuccess());
            assertEquals(rTaskToken.getTask().getToken(), executionToken);
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
        String executionToken = "testTaskExecutionWithToken";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        DiscreteBrokerConfig config = null;
        RTask rTask = null;
        RTaskToken rTaskToken = null;
        RTaskResult rTaskResult = null;

        // Test error handling.
        Exception exception = null;
        String exceptionMsg = "";
        Exception cleanupException = null;
        String cleanupExceptionMsg = "";

        // Test.
        config = new DiscreteBrokerConfig(endpoint);

        try {
            rBroker = RBrokerFactory.discreteTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.discreteTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * createDiscreteTaskOptions(false) returns "bad" config.
                 */ 
                DiscreteTaskOptions options =
                    DeployRUtil.createDiscreteTaskOptions(false);
                rTask = RTaskFactory.discreteTask("Histogram of Auto Sales",
                                         "root", "testuser", null, options);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.discreteTask failed: ";
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
        if (exception == null) {
            assertNotNull(rTaskResult);
            assertFalse(rTaskResult.isSuccess());
            assertNotNull(rTaskResult.getFailure());
        } else {
            fail(exceptionMsg + exception.getMessage());
        }

        // Test cleanup errors.
        if (cleanupException != null) {
            fail(cleanupExceptionMsg + cleanupException.getMessage());
        }
    }
 
}
