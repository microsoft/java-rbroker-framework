/*
 * RTask.java
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
 * Represents any R Analytics task for execution on an
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public interface RTask {

    /**
     * Return RTask client application token.
     *
     * Tokens can be used to "tag" an RTask with a client application
     * specific token. These tokens can be then be used by a client
     * application when an RTask completes to decide how best to process
     * or direct the RTaskResult.
     *
     * For example, if multiple "services" within a single application
     * are using a single instance of RBroker, then each RTask submitted to
     * that RBroker could be tagged with the name of the originating service.
     */
    Object getToken();

    /**
     * Set an RTask client application token.
     *
     * Tokens can be used to "tag" an RTask with a client application
     * specific token. These tokens can be then be used by a client
     * application when an RTask completes to decide how best to process
     * or direct the RTaskResult.
     *
     * For example, if multiple "services" within a single application
     * are using a single instance of RBroker, then each RTask submitted to
     * that RBroker could be tagged with the name of the originating service.
     */
    void setToken(Object token);
}
