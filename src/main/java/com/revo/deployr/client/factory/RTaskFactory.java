/*
 * RTaskFactory.java
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
package com.revo.deployr.client.factory;

import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.options.BackgroundTaskOptions;
import com.revo.deployr.client.broker.options.DiscreteTaskOptions;
import com.revo.deployr.client.broker.options.PooledTaskOptions;
import com.revo.deployr.client.broker.task.BackgroundTask;
import com.revo.deployr.client.broker.task.DiscreteTask;
import com.revo.deployr.client.broker.task.PooledTask;

import java.net.URL;

/**
 * Factory to simplify the creation of Discrete, Pooled and Background
 * {@link com.revo.deployr.client.broker.RTask} instances.
 */
public class RTaskFactory {

    private RTaskFactory() {
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.DiscreteTask}
     * for an analytics Web service based on a repository-managed R script.
     */
    public static RTask discreteTask(String filename,
                                     String directory,
                                     String author,
                                     String version,
                                     DiscreteTaskOptions options) {

        return new DiscreteTask(filename,
                directory,
                author,
                version,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.DiscreteTask}
     * for an analytics Web service based on a URL-addressable R script.
     */
    public static RTask discreteTask(URL externalURL,
                                     DiscreteTaskOptions options) {

        return new DiscreteTask(externalURL,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.PooledTask}
     * for an analytics Web service based on a repository-managed R script.
     */
    public static RTask pooledTask(String filename,
                                   String directory,
                                   String author,
                                   String version,
                                   PooledTaskOptions options) {

        return new PooledTask(filename,
                directory,
                author,
                version,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.PooledTask}
     * for an analytics Web service based on an arbitrary block of R code.
     */
    public static RTask pooledTask(String codeBlock,
                                   PooledTaskOptions options) {

        return new PooledTask(codeBlock,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.PooledTask}
     * for an analytics Web service based on a URL-addressable R script.
     */
    public static RTask pooledTask(URL externalURL,
                                   PooledTaskOptions options) {

        return new PooledTask(externalURL,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.BackgroundTask}
     * for an analytics Web service based on a repository-managed R script.
     */
    public static RTask backgroundTask(String taskName,
                                       String taskDescription,
                                       String filename,
                                       String directory,
                                       String author,
                                       String version,
                                       BackgroundTaskOptions options) {

        return new BackgroundTask(taskName,
                taskDescription,
                filename,
                directory,
                author,
                version,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.BackgroundTask}
     * for an analytics Web service based on an arbitrary block of R code.
     */
    public static RTask backgroundTask(String taskName,
                                       String taskDescription,
                                       String codeBlock,
                                       BackgroundTaskOptions options) {

        return new BackgroundTask(taskName,
                taskDescription,
                codeBlock,
                options);
    }

    /**
     * Create an instance of a
     * {@link com.revo.deployr.client.broker.task.BackgroundTask}
     * for an analytics Web service based on a URL-addressable R script.
     */
    public static RTask backgroundTask(String taskName,
                                       String taskDescription,
                                       URL externalURL,
                                       BackgroundTaskOptions options) {

        return new BackgroundTask(taskName,
                taskDescription,
                externalURL,
                options);
    }

}
