/*
 * DiscreteBrokerConfig.java
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
 * Configuration options for a Discrete Task
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public class DiscreteBrokerConfig extends RBrokerConfig {

    public DiscreteBrokerConfig(String deployrEndpoint) {

        super(deployrEndpoint, null);
    }

    public DiscreteBrokerConfig(String deployrEndpoint,
                                RAuthentication userCredentials) {

        super(deployrEndpoint, userCredentials, 1);
    }

    public DiscreteBrokerConfig(String deployrEndpoint,
                                RAuthentication userCredentials,
                                int maxConcurrentTaskLimit) {

        super(deployrEndpoint, userCredentials, maxConcurrentTaskLimit);
    }

}
