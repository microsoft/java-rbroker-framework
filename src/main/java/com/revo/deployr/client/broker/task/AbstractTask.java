/*
 * AbstractTask.java
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
package com.revo.deployr.client.broker.task;

import com.revo.deployr.client.broker.RTask;

/**
 * Represents an abstract {@link com.revo.deployr.client.broker.RTask}
 * for execution on an
 * {@link com.revo.deployr.client.broker.RBroker}.
 * Concrete implementations provided by
 * {@link com.revo.deployr.client.broker.task.DiscreteTask},
 * {@link com.revo.deployr.client.broker.task.PooledTask} and
 * {@link com.revo.deployr.client.broker.task.BackgroundTask}.
 */
public abstract class AbstractTask implements RTask {

    /*
     * RTask token, optionally assigned by a client application
     * as a form of "tag" on the task.
     */
    protected Object token;

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }
}
