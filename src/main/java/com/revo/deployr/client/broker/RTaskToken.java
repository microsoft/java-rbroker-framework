/*
 * RTaskToken.java
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
package com.revo.deployr.client.broker;

import com.revo.deployr.client.broker.impl.RTaskTokenListener;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Represents a handle to an {@link com.revo.deployr.client.broker.RTask}
 * live on an {@link com.revo.deployr.client.broker.RBroker}.
 */
public interface RTaskToken extends RTaskTokenListener {

    public RTask getTask();

    public RTaskResult getResult()
            throws InterruptedException,
            CancellationException,
            ExecutionException;

    public Future getFuture();

    public boolean isDone();

    public boolean isCancelled();

    public boolean cancel(boolean mayInterruptIfRunning);
}
