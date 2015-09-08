/*
 * RTaskType.java
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

/**
 * <p>
 * Defines the currently supported set of
 * {@link com.revo.deployr.client.broker.RTask}.
 * </p>
 * Each {@link com.revo.deployr.client.broker.RTaskResult}
 * identifies it's type on
 * {@link com.revo.deployr.client.broker.RTaskResult#getType}.
 */
public enum RTaskType {
    /**
     * Discrete task.
     */
    DISCRETE,
    /**
     * Pooled task.
     */
    POOLED,
    /**
     * Background task.
     */
    BACKGROUND
}
