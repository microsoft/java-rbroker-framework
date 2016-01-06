/*
 * PooledTaskBrokerTest.java
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

public class PooledTaskBrokerTest {

    String endpoint = null;
    boolean allowSelfSigned = false;

    public PooledTaskBrokerTest() {
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
            if (endpoint == null) {
                fail("setUp: connection.[protocol|endpoint] null.");
            }
            allowSelfSigned = 
                Boolean.valueOf(System.getProperty("allow.SelfSignedSSLCert"));
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
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
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
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(DeployRUtil.BAD_ENDPOINT, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
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
     * Test authenticated PooledBrokerConfig.
     */
    @Test
    public void testConfigAuthenticationGoodCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test authenticated PooledBrokerConfig using bad credentials.
     */
    @Test
    public void testConfigAuthenticationBadCredentials() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
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
     * Test PooledBrokerConfig using custom pool size.
     */
    @Test
    public void testConfigCustomPoolSize() {

        // Test variables.
        int MAX_CONCURRENCY = 10;
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth, MAX_CONCURRENCY);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test PooledBrokerConfig using custom pool creation options.
     */
    @Test
    public void testConfigPoolCreationWithGoodOptions() {

        // Test variables.
        int MAX_CONCURRENCY = 10;
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PoolCreationOptions options = null;
        PooledBrokerConfig config = null;
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

        /*
         * createPoolCreationOptions(true) returns "good" config.
         */ 
        options = DeployRUtil.createPoolCreationOptions(true);
        config = new PooledBrokerConfig(endpoint,
                                        rAuth,
                                        MAX_CONCURRENCY,
                                        options);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test PooledBrokerConfig using "bad" custom pool
     * creation options that can be relied upon to
     * cause pool initialization failures. 
     */
    @Test
    public void testConfigPoolCreationWithBadOptions() {

        // Test variables.
        int MAX_CONCURRENCY = 10;
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PoolCreationOptions options = null;
        PooledBrokerConfig config = null;
        RTask rTask = null;
        RTaskResult rTaskResult = null;
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
      
        /*
         * createPoolCreationOptions(false) returns "bad" config.
         */ 
        options = DeployRUtil.createPoolCreationOptions(false);
        config = new PooledBrokerConfig(endpoint,
                                        rAuth,
                                        MAX_CONCURRENCY,
                                        options);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
            isRejected = false;
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
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
     * Test RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenStandardQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenStandardQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test PooledBrokerConfig RTask
     * priority execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenPriorityQueueGood() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenPriorityQueueGood";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
        PooledBrokerConfig config = null;
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
        int multipleTaskTestSize = 10;
        int multipleTaskMaxConcurrency = 10;
        rAuth =
            new RBasicAuthentication(System.getProperty("username"),
                                     System.getProperty("password"));
        config = new PooledBrokerConfig(endpoint, rAuth, multipleTaskMaxConcurrency);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {

            for(int i=0; i<multipleTaskTestSize; i++) {
                try {
                    RTask rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                             "root", "testuser", null, null);
                    rTasks.add(rTask);
                } catch (Exception ex) {
                    exception = ex;
                    exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test "bad" repository-managed script based
     * RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenBadScript() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenBadScript";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * Repository-managed script does not existe, will
                 * result in an execution error.
                 */
                rTask = RTaskFactory.pooledTask("This Script Does Not Exist",
                                         "root", "testuser", null, null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test "bad" code-block based RTask execution with execution-token.
     */
    @Test
    public void testTaskExecutionWithTokenBadCode() {

        // Test variables.
        String executionToken = "testTaskExecutionWithTokenBadCode";
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                /*
                 * R code block will result in a syntax
                 * error being raised by the R interpreter.
                 */
                rTask = RTaskFactory.pooledTask("x y", null);
                rTask.setToken(executionToken);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
     * Test RTask execution with "good" task execution options.
     */
    @Test
    public void testTaskExecutionWithGoodOptions() {

        // Test variables.
        RBroker rBroker = null;
        RBasicAuthentication rAuth = null;
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                PooledTaskOptions options =
                    DeployRUtil.createPooledTaskOptions(true);
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, options);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
        PooledBrokerConfig config = null;
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
        config = new PooledBrokerConfig(endpoint, rAuth);
        config.allowSelfSignedSSLCert = allowSelfSigned;

        try {
            rBroker = RBrokerFactory.pooledTaskBroker(config);
        } catch (Exception ex) {
            exception = ex;
            exceptionMsg = "RBrokerFactory.pooledTaskBroker failed: ";
        }

        if(rBroker != null) {
            try {
                PooledTaskOptions options =
                    DeployRUtil.createPooledTaskOptions(false);
                rTask = RTaskFactory.pooledTask("Histogram of Auto Sales",
                                         "root", "testuser", null, options);
            } catch (Exception ex) {
                exception = ex;
                exceptionMsg = "RTaskFactory.pooledTask failed: ";
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
