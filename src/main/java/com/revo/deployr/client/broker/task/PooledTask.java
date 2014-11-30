/*
 * PooledTask.java
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

import com.revo.deployr.client.broker.options.PooledTaskOptions;

import java.net.URL;

/**
 * Represents a Pooled {@link com.revo.deployr.client.broker.RTask}
 * for execution on an
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public class PooledTask extends AbstractTask {

    /*
     * Code property represents a block of executable R code.
     */
    public final String code;

    /*
     * Exteral property represents a URL/path to an external script
     * or chain of scripts.
     */
    public String external;

    /*
     * Filename, directory, author and optional version
     * properties represent a repository-managed executable R script.
     */
    public final String filename;
    public final String directory;
    public final String author;
    public final String version;

    /*
     * Options customize the pre-execution, on-execution
     * and post-execution handling of the task on DeployR.
     */
    public final PooledTaskOptions options;

    public PooledTask(String code,
                      PooledTaskOptions options) {

        this.code = code;
        this.filename = null;
        this.directory = null;
        this.author = null;
        this.version = null;
        this.external = null;
        this.options = options;
    }

    public PooledTask(URL externalURL,
                      PooledTaskOptions options) {

        this.code = null;
        this.filename = null;
        this.directory = null;
        this.author = null;
        this.version = null;
        this.external = externalURL.toString();
        this.options = options;
    }

    public PooledTask(String filename,
                      String directory,
                      String author,
                      String version,
                      PooledTaskOptions options) {

        this.code = null;
        this.filename = filename;
        this.directory = directory;
        this.author = author;
        this.version = version;
        this.external = null;
        this.options = options;
    }

    public String toString() {

        if (code != null) {
            return "PooledTask: [ " + code + " ]";
        } else if (external != null) {
            return "PooledTask: [ " + external + " ]";
        } else {
            return "PooledTask: [ " + filename + " , " +
                    directory + " , " +
                    author + " , " +
                    version + " ]";
        }
    }

}
