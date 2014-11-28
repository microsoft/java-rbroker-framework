/*
 * RTaskTokenImpl.java
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
package com.revo.deployr.client.broker.impl;

import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.RTaskResult;
import com.revo.deployr.client.broker.RTaskToken;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*
 * RTaskTokenImpl.
 */
public class RTaskTokenImpl implements RTaskToken {

    private final RTask task;
    private RTaskResult result;
    private Future future;

    public RTaskTokenImpl(RTask task) {
        this.task = task;
    }

    public RTaskTokenImpl(RTask task, Future future) {
        this.task = task;
        this.future = future;
    }

    public final RTask getTask() {
        return task;
    }

    public RTaskResult getResult()
            throws InterruptedException,
            CancellationException,
            ExecutionException {

        if (result != null) {
            return result;
        } else {
            while (future == null) {
                try {
                    Thread.currentThread().sleep(250);
                } catch (InterruptedException iex) {
                    throw iex;
                }
            }
            return (RTaskResult) future.get();
        }
    }

    public final Future getFuture() {
        return future;
    }

    public boolean isDone() {
        return (future != null) ? future.isDone() : false;
    }

    public boolean isCancelled() {
        return (future != null) ? future.isCancelled() : false;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {

        if (result != null) {
            // RTask completed, can not be cancelled.
            return false;
        } else if (future != null) {
            // RTask completed, can not be cancelled.
            if (future.isDone() || future.isCancelled()) {
                return false;
            }

            // Delegate cancel operation to Future.cancel().
            return future.cancel(mayInterruptIfRunning);

        } else {
            // RTask still pending onTask() confirmation
            // from RBroker, can not be cancelled.
            return false;
        }
    }

    /*
     * RTaskListener Interface methods.
     */
    public void onTask(RTask task,
                       Future future) {

        this.future = future;
    }


}
