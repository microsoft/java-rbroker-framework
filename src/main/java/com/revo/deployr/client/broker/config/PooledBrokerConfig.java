/*
 * PooledBrokerConfig.java
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
import com.revo.deployr.client.broker.options.PoolCreationOptions;

/**
 * Configuration options for a Pooled Task
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public class PooledBrokerConfig extends RBrokerConfig {

    /**
     * <p>    
     * Specifies the set of pre-initialization operations to be
     * performed on each R Session in the pool at creation time.
     * </p>
     * <p>     
     * For example, R workspace data, such as R models, can be
     * preloaded into each R Session in the pool at startup.
     * Data files, such as CSV, XLS can be preloaded into the
     * working directory for each R Session in the pool at startup.
     * </p>
     * <p>     
     * Preloading binary R data or file data at startup ensures
     * the overhead associated with runtime dependencies for each
     * {@link com.revo.deployr.client.broker.RTask} can be kept
     * to a minimum at runtime.
     * </p>
     * Using pool creation options intelligently can greatly help
     * to improve overall {@link com.revo.deployr.client.broker.RTask}
     * throughput on the DeployR server.
     */
    public final PoolCreationOptions poolCreationOptions;

    public PooledBrokerConfig(String deployrEndpoint,
                              RAuthentication userCredentials) {

        super(deployrEndpoint, userCredentials, 1);
        this.poolCreationOptions = null;
    }

    public PooledBrokerConfig(String deployrEndpoint,
                              RAuthentication userCredentials,
                              int maxConcurrentTaskLimit) {

        super(deployrEndpoint, userCredentials, maxConcurrentTaskLimit);
        this.poolCreationOptions = null;
    }

    public PooledBrokerConfig(String deployrEndpoint,
                              RAuthentication userCredentials,
                              int maxConcurrentTaskLimit,
                              PoolCreationOptions poolCreationOptions) {

        super(deployrEndpoint, userCredentials, maxConcurrentTaskLimit);
        this.poolCreationOptions = poolCreationOptions;
    }

}
