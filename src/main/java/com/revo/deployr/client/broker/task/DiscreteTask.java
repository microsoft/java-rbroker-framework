/*
 * DiscreteTask.java
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
package com.revo.deployr.client.broker.task;

import com.revo.deployr.client.broker.RTask;
import com.revo.deployr.client.broker.options.DiscreteTaskOptions;

import java.net.URL;

/**
 * Represents a Discrete {@link com.revo.deployr.client.broker.RTask}
 * for execution on an
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public class DiscreteTask implements RTask {

    /*
     * Filename, directory, author and optional version
     * properties represent a repository-managed executable R script.
     */
    public final String filename;
    public final String directory;
    public final String author;
    public final String version;

    /*
     * Exteral property represents a URL/path to an external script
     * or chain of scripts. External execution is available only
     * on an instance of an RBroker that has been passed an
     * RAuthentication on it's DiscreteBrokerConfig.
     */
    public final String external;

    /*
     * Options customize the pre-execution, on-execution
     * and post-execution handling of the task on DeployR.
     */
    public final DiscreteTaskOptions options;

    public DiscreteTask(String filename,
                        String directory,
                        String author,
                        String version,
                        DiscreteTaskOptions options) {

        this.filename = filename;
        this.directory = directory;
        this.author = author;
        this.version = version;
        this.external = null;
        this.options = options;
    }

    public DiscreteTask(URL externalURL,
                        DiscreteTaskOptions options) {

        this.filename = null;
        this.directory = null;
        this.author = null;
        this.version = null;
        this.external = externalURL.toString();
        this.options = options;
    }


    public String toString() {
        return "DiscreteTask: [ " + filename + " , " +
                directory + " , " +
                author + " , " +
                version + " ]";
    }
}
