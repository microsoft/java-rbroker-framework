/*
 * TaskOptions.java
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
 * Task options allow the customization of pre-execution.
 * on-execution and post-execution behaviors for a given
 * Task.
 */
public class TaskOptions {

    public TaskOptions() {
    }

    /**
     * [Pre-execution] List of DeployR-encoded R objects to be added
     * to the workspace of the current R session prior to the execution.
     */
    public List<RData> rinputs;

    /**
     * <p>
     * [Pre-execution] Comma-seperated list of primitive R object names
     * and values, to be added to the workspace of the current R session
     * prior to the execution.
     * </p>
     * eg. csvrinputs=name,George,age,45
     */
    public String csvrinputs;

    /**
     * [Pre-execution] Preload workspace options allow the loading of one
     * or more binary R objects from the repository into the
     * workspace of the current R session prior to execution.
     */
    public TaskPreloadOptions preloadWorkspace;

    /**
     * [Pre-execution] Preload working directory options allow the
     * loading of one or more files from the repository into the
     * working directory of the current R session prior to execution.
     */
    public TaskPreloadOptions preloadDirectory;

    /**
     * <p>
     * [Pre-execution] Preload by directory option allows the
     * loading of all files from one or more repository-managed
     * directories into the working directory of the current R session
     * prior to execution.
     * </p>
     * When loading the contents of more than one directory,
     * use a comma-separated list of directory names.
     */
    public String preloadByDirectory;

    /**
     * [On-execution] Set R graphics device to use on execution:
     * "png" or "svg". The default R graphics device is "png".
     */
    public String graphicsDevice;

    /**
     * [On-execution] Set the width of the R graphics device
     * on execution.
     */
    public int graphicsWidth;

    /**
     * [On-execution] Set the height of the R graphics device
     * on execution.
     */
    public int graphicsHeight;

    /**
     * [On-execution] When enabled R commands are not echoed to
     * the console output and will not appear in response markup or
     * on the event stream.
     */
    public boolean echooff;

    /**
     * [On-execution] When enabled all R console output is
     * suppressed and will not appear in response markup. This
     * control has no impact on console output on the event stream.
     */
    public boolean consoleoff;

    /**
     * [On-execution] When enabled artfiacts generated in the
     * working directory are not cached in the database and
     * will not appear in response markup. This control has
     * no impact on console output on the event stream.
     */
    public boolean artifactsoff;

    /**
     * [Post-execution] List of workspace objects to be retrieved
     * from the workspace of the current R session following
     * the execution and returned as DeployR-encoded R objects.
     */
    public List<String> routputs;

    /**
     * <p>
     * [Post-execution] Workspace data.frame object encoding
     * preference when retrieving R objects from the current
     * R session following a Task execution.
     * <p>
     * This option works in conjunction with the robjects property
     * on this class. The default DeployR-encoding is to encode
     * primatives inside data.frame objects as primitives,
     * not as vectors.
     */
    public boolean encodeDataFramePrimitiveAsVector;

    /**
     * [Post-execution] Optional custom value to denote NAN
     * values in DeployR-encoded objects in the response markup.
     * Default is null.
     */
    public String nan;

    /**
     * [Post-execution] Optional custom value to denote INFINITY
     * values in DeployR-encoded objects in the response markup.
     * Default is 0x7ff0000000000000L.
     */
    public String infinity;

    /**
     * <p>
     * [Post-execution] Repository storage options allow the storage
     * of one-or-more workspace objects, the entire workspace
     * and/or one-or-more working directory files from the
     * current R session into the repository following the execution.
     * </p>
     * Storage options are only available to Tasks executing
     * on behalf of AUTHENTICATED users. Tasks executing on behalf
     * of ANONYMOUS users can not store data to the repository.
     */
    public TaskStorageOptions storageOptions;

}
