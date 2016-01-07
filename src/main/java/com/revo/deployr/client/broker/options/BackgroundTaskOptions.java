/*
 * BackgroundTaskOptions.java
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
package com.revo.deployr.client.broker.options;

/**
 * Runtime options for a
 * {@link com.revo.deployr.client.broker.task.BackgroundTask}.
 */
public class BackgroundTaskOptions extends TaskOptions {

    /**
     * Enable noproject option if project persistence
     * following task execution is not required.
     */
    public boolean noproject;

    /**
     * Background task schedule repeat count.
     */
    public int repeatCount;

    /**
     * Background task schedule repeat interval.
     */
    public long repeatInterval;

    /**
     * Background task schedule start time in UTC.
     */
    public long startTime;
}
