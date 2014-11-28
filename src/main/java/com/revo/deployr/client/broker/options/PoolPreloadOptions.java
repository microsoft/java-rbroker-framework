/*
 * PoolPreloadOptions.java
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
package com.revo.deployr.client.broker.options;

/**
 * Pool preload options. Supports the loading of DeployR-managed
 * repository files into the R session during pool initialization.
 */
public class PoolPreloadOptions {

    public PoolPreloadOptions() {
    }

    /**
     * Comma-separated list of repository filenames.
     */
    public String filename;

    /**
     * Comma-separated list of directories, one directory per filename.
     */
    public String directory;

    /**
     * Comma-separated list of authors, one author per filename.
     */
    public String author;

    /**
     * Optional, comma-separated list of versions, one version per filename.
     */
    public String version;

}
