/*
 * RBrokerFactory.java
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
package com.revo.deployr.client.factory;

import com.revo.deployr.client.RClientException;
import com.revo.deployr.client.RDataException;
import com.revo.deployr.client.RGridException;
import com.revo.deployr.client.RSecurityException;
import com.revo.deployr.client.broker.RBroker;
import com.revo.deployr.client.broker.RBrokerException;
import com.revo.deployr.client.broker.config.BackgroundBrokerConfig;
import com.revo.deployr.client.broker.config.DiscreteBrokerConfig;
import com.revo.deployr.client.broker.config.PooledBrokerConfig;
import com.revo.deployr.client.broker.engine.BackgroundTaskBroker;
import com.revo.deployr.client.broker.engine.DiscreteTaskBroker;
import com.revo.deployr.client.broker.engine.PooledTaskBroker;

/**
 * Factory to simplify the creation of Discrete, Pooled and Background
 * {@link com.revo.deployr.client.broker.RBroker} instances.
 */
public class RBrokerFactory {

    private RBrokerFactory() {
    }

    /**
     * Create an instance of an
     * {@link com.revo.deployr.client.broker.RBroker}
     * to manage the execution of
     * {@link com.revo.deployr.client.broker.task.DiscreteTask}.
     *
     * @param brokerConfig {@link com.revo.deployr.client.broker.config.DiscreteBrokerConfig}
     *                     configuration details.
     * @return {@link com.revo.deployr.client.broker.RBroker}
     */
    public static RBroker discreteTaskBroker(
            DiscreteBrokerConfig brokerConfig)
            throws RClientException,
            RSecurityException,
            RDataException,
            RGridException,
            RBrokerException {

        return new DiscreteTaskBroker(brokerConfig);
    }

    /**
     * Create an instance of an
     * {@link com.revo.deployr.client.broker.RBroker}
     * to manage the execution of
     * {@link com.revo.deployr.client.broker.task.PooledTask}.
     *
     * @param brokerConfig {@link com.revo.deployr.client.broker.config.PooledBrokerConfig}
     *                     configuration details.
     * @return {@link com.revo.deployr.client.broker.RBroker}
     */
    public static RBroker pooledTaskBroker(
            PooledBrokerConfig brokerConfig)
            throws RClientException,
            RSecurityException,
            RDataException,
            RGridException,
            RBrokerException {

        return new PooledTaskBroker(brokerConfig);
    }

    /**
     * Create an instance of an
     * {@link com.revo.deployr.client.broker.RBroker}
     * to manage the execution of
     * {@link com.revo.deployr.client.broker.task.BackgroundTask}.
     *
     * @param brokerConfig {@link com.revo.deployr.client.broker.config.BackgroundBrokerConfig}
     *                     configuration details.
     * @return {@link com.revo.deployr.client.broker.RBroker}
     */
    public static RBroker backgroundTaskBroker(
            BackgroundBrokerConfig brokerConfig)
            throws RClientException,
            RSecurityException,
            RDataException,
            RGridException,
            RBrokerException {

        return new BackgroundTaskBroker(brokerConfig);
    }

}
