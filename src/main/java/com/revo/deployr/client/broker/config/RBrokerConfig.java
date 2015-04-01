/*
 * RBrokerConfig.java
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
package com.revo.deployr.client.broker.config;

import com.revo.deployr.client.auth.RAuthentication;

/**
 * Base class for all {@link com.revo.deployr.client.broker.RBroker}
 * configuration options.
 */

public abstract class RBrokerConfig {

    /**
     * Constant value, defines the max permissible concurrency
     * supported by a single instance of
     * an {@link com.revo.deployr.client.broker.RBroker}.
     */
    public static final int MAX_CONCURRENCY = 999;

    /**
     * Specifies the HTTP URL endpoint for a DeployR Server instance.
     * <p/>
     * For example: http://dserver:dport/deployr
     */
    public final String deployrEndpoint;

    /**
     * Specifies the basic authentication user credentials for
     * an authenticated instance of an
     * {@link com.revo.deployr.client.broker.RBroker}.
     * <p/>
     * An authenticated instance of an
     * {@link com.revo.deployr.client.broker.RBroker} should be
     * used when you want {@link com.revo.deployr.client.broker.RTask}
     * to execute on the DeployR server with the full permissions and
     * access rights of the user indicated by the credentials.
     */
    public final RAuthentication userCredentials;

    /**
     * Specifies the task execution concurrency levels requested for
     * an instance of an {@link com.revo.deployr.client.broker.RBroker}.
     * <p/>
     * Specifying a value of 1 will result in serial execution for
     * all tasks submitted to the
     * {@link com.revo.deployr.client.broker.RBroker}.
     * <p/>
     * Specifying a value greater than 1 will result in concurrent
     * execution for tasks submitted to the
     * {@link com.revo.deployr.client.broker.RBroker}. In all cases,
     * up to maxConcurrentTaskLimit
     * {@link com.revo.deployr.client.broker.RTask} will execute
     * concurrently.
     * <p/>
     * Regardless of what task execution concurrency level you use
     * the execution of tasks will always proceed in a FIFO order.
     * <p/>
     * Regardless of the concurrency levels requested when creating an
     * instance of {@link com.revo.deployr.client.broker.RBroker}, a
     * combination of DeployR Server Policies and available DeployR Grid
     * resources determine the actual task throughput that will be
     * achieved on the DeployR server.
     * <p/>
     * Where an instance
     * of an {@link com.revo.deployr.client.broker.RBroker} has been
     * configured for concurrency levels in excess of those supported by
     * your DeployR Server Policies or DeployR Grid resources, some tasks
     * when executed will likely result in
     * {@link com.revo.deployr.client.RGridException}. Such tasks can be
     * resubmitted to the {@link com.revo.deployr.client.broker.RBroker}
     * for execution at a later date. If
     * {@link com.revo.deployr.client.RGridException} are seen frequently
     * we recommend adjusting the appropriate DeployR Server Policies
     * and/or increasing the resources allocated to the DeployR Grid.
     */
    public int maxConcurrentTaskLimit;

    /*
     * Enable this property if you want to accept connections
     * to a DeployR server that is using a self-signed
     * SSL certificate.
     */
    public boolean allowSelfSignedSSLCert = false;

    public RBrokerConfig(String deployrEndpoint) {
        this(deployrEndpoint, null, 1);
    }

    public RBrokerConfig(String deployrEndpoint,
                         boolean allowSelfSignedSSLCert) {
        this(deployrEndpoint, null, allowSelfSignedSSLCert);
    }

    public RBrokerConfig(String deployrEndpoint,
                         RAuthentication userCredentials) {

        this.deployrEndpoint = deployrEndpoint;
        this.userCredentials = userCredentials;
        // Default: single threaded executor,
        // resulting in serial task execution.
        this.maxConcurrentTaskLimit = 1;
    }

    public RBrokerConfig(String deployrEndpoint,
                         RAuthentication userCredentials,
                         boolean allowSelfSignedSSLCert) {

        this.deployrEndpoint = deployrEndpoint;
        this.userCredentials = userCredentials;
        // Default: single threaded executor,
        // resulting in serial task execution.
        this.maxConcurrentTaskLimit = 1;
        this.allowSelfSignedSSLCert = allowSelfSignedSSLCert;
    }

    public RBrokerConfig(String deployrEndpoint,
                         RAuthentication userCredentials,
                         int maxConcurrentTaskLimit) {

        this.deployrEndpoint = deployrEndpoint;
        this.userCredentials = userCredentials;
        this.maxConcurrentTaskLimit = maxConcurrentTaskLimit;
    }

    public RBrokerConfig(String deployrEndpoint,
                         RAuthentication userCredentials,
                         int maxConcurrentTaskLimit,
                         boolean allowSelfSignedSSLCert) {

        this.deployrEndpoint = deployrEndpoint;
        this.userCredentials = userCredentials;
        this.maxConcurrentTaskLimit = maxConcurrentTaskLimit;
        this.allowSelfSignedSSLCert = allowSelfSignedSSLCert;
    }

}
