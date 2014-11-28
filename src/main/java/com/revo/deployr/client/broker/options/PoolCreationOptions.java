/*
 * PoolCreationOptions.java
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

import com.revo.deployr.client.data.RData;

import java.util.List;

/**
 * Startup options for a Pooled Task
 * {@link com.revo.deployr.client.broker.RBroker}.
 */
public class PoolCreationOptions {

    /**
     * Preload working directory options allow the loading of
     * one or more files from the repository into the working
     * directory of each R session in the pool on pool initialization.
     */
    public PoolPreloadOptions preloadDirectory;

    /**
     * Preload workspace options allow the loading of
     * one or more binary R objects from the repository into the
     * workspace of each R session in the pool on pool initialization.
     */
    public PoolPreloadOptions preloadWorkspace;

    /**
     * Preload by directory option allows the
     * loading of all files from one or more repository-managed
     * directories into the working directory into the working
     * directory of each R session in the pool on pool initialization.
     * <p/>
     * When loading the contents of more than one directory,
     * use a comma-separated list of directory names.
     */
    public String preloadByDirectory;

    /**
     * Rinputs allow the loading of one or more DeployR-encoded
     * R objects to be added to the workspace of each R session
     * in the pool on pool initialization.
     */
    public List<RData> rinputs;

    /**
     * ReleaseGridResources when enabled causes all live grid
     * resources held by the user to be released before the new pool
     * is created.
     * <p/>
     * This is particularly useful if your client application needs to
     * create a new pool having lost its connection to an original pool.
     * For example, due to a network connection failure between your
     * client application and the DeployR server.
     */
    public boolean releaseGridResources;

}
