/*
 * RBrokerListener.java
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
package com.revo.deployr.client.broker;

/**
 * Asynchronous callback interface for
 * {@link com.revo.deployr.client.broker.RBroker}
 * runtime statistics and error event listeners.
 */
public interface RBrokerListener {

    /**
     * Asynchronous callback notification when a runtime
     * error occurs within
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public void onRuntimeError(Throwable throwable);

    /**
     * Asynchronous callback notification
     * {@link com.revo.deployr.client.broker.RBrokerRuntimeStats}.
     */
    public void onRuntimeStats(RBrokerRuntimeStats stats,
                               int maxConcurrency);
}
