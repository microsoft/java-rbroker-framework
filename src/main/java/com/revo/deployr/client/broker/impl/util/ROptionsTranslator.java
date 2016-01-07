/*
 * ROptionsTranslator.java
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
package com.revo.deployr.client.broker.impl.util;

import com.revo.deployr.client.broker.options.BackgroundTaskOptions;
import com.revo.deployr.client.broker.options.DiscreteTaskOptions;
import com.revo.deployr.client.broker.options.PoolCreationOptions;
import com.revo.deployr.client.broker.options.PooledTaskOptions;
import com.revo.deployr.client.params.*;

public class ROptionsTranslator {

    public static AnonymousProjectExecutionOptions translate(
            DiscreteTaskOptions taskOptions) {

        AnonymousProjectExecutionOptions options = null;

        if (taskOptions != null) {

            options = new AnonymousProjectExecutionOptions();

            /*
             * DiscreteTaskOptions to ProjectExecutionOptions.
             */

            options.rinputs = taskOptions.rinputs;
            options.csvrinputs = taskOptions.csvrinputs;

            if (taskOptions.preloadWorkspace != null) {

                options.preloadWorkspace = new ProjectPreloadOptions();
                options.preloadWorkspace.filename =
                        taskOptions.preloadWorkspace.filename;
                options.preloadWorkspace.directory =
                        taskOptions.preloadWorkspace.directory;
                options.preloadWorkspace.author =
                        taskOptions.preloadWorkspace.author;
                options.preloadWorkspace.version =
                        taskOptions.preloadWorkspace.version;
            }

            if (taskOptions.preloadDirectory != null) {

                options.preloadDirectory = new ProjectPreloadOptions();
                options.preloadDirectory.filename =
                        taskOptions.preloadDirectory.filename;
                options.preloadDirectory.directory =
                        taskOptions.preloadDirectory.directory;
                options.preloadDirectory.author =
                        taskOptions.preloadDirectory.author;
                options.preloadDirectory.version =
                        taskOptions.preloadDirectory.version;
            }

            options.preloadByDirectory = taskOptions.preloadByDirectory;

            options.graphicsDevice = taskOptions.graphicsDevice;
            options.graphicsWidth = taskOptions.graphicsWidth;
            options.graphicsHeight = taskOptions.graphicsHeight;
            options.echooff = taskOptions.echooff;
            options.consoleoff = taskOptions.consoleoff;
            options.artifactsoff = taskOptions.artifactsoff;
            options.routputs = taskOptions.routputs;
            options.encodeDataFramePrimitiveAsVector =
                    taskOptions.encodeDataFramePrimitiveAsVector;
            options.nan = taskOptions.nan;
            options.infinity = taskOptions.infinity;

            if (taskOptions.storageOptions != null) {

                options.storageOptions = new ProjectStorageOptions();
                options.storageOptions.directory =
                        taskOptions.storageOptions.directory;
                options.storageOptions.files =
                        taskOptions.storageOptions.files;
                options.storageOptions.newversion =
                        taskOptions.storageOptions.newversion;
                options.storageOptions.objects =
                        taskOptions.storageOptions.objects;
                options.storageOptions.published =
                        taskOptions.storageOptions.published;
                options.storageOptions.workspace =
                        taskOptions.storageOptions.workspace;
            }

        }

        return options;
    }

    public static ProjectExecutionOptions translate(
            PooledTaskOptions taskOptions) {

        ProjectExecutionOptions options = null;

        if (taskOptions != null) {

            options = new ProjectExecutionOptions();

            /*
             * PooledTaskOptions to ProjectExecutionOptions.
             */

            options.rinputs = taskOptions.rinputs;
            options.csvrinputs = taskOptions.csvrinputs;

            if (taskOptions.preloadWorkspace != null) {

                options.preloadWorkspace = new ProjectPreloadOptions();
                options.preloadWorkspace.filename =
                        taskOptions.preloadWorkspace.filename;
                options.preloadWorkspace.directory =
                        taskOptions.preloadWorkspace.directory;
                options.preloadWorkspace.author =
                        taskOptions.preloadWorkspace.author;
                options.preloadWorkspace.version =
                        taskOptions.preloadWorkspace.version;
            }

            if (taskOptions.preloadDirectory != null) {

                options.preloadDirectory = new ProjectPreloadOptions();
                options.preloadDirectory.filename =
                        taskOptions.preloadDirectory.filename;
                options.preloadDirectory.directory =
                        taskOptions.preloadDirectory.directory;
                options.preloadDirectory.author =
                        taskOptions.preloadDirectory.author;
                options.preloadDirectory.version =
                        taskOptions.preloadDirectory.version;
            }

            options.preloadByDirectory = taskOptions.preloadByDirectory;

            options.graphicsDevice = taskOptions.graphicsDevice;
            options.graphicsWidth = taskOptions.graphicsWidth;
            options.graphicsHeight = taskOptions.graphicsHeight;
            options.echooff = taskOptions.echooff;
            options.consoleoff = taskOptions.consoleoff;
            options.artifactsoff = taskOptions.artifactsoff;
            options.routputs = taskOptions.routputs;
            options.encodeDataFramePrimitiveAsVector =
                    taskOptions.encodeDataFramePrimitiveAsVector;
            options.nan = taskOptions.nan;
            options.infinity = taskOptions.infinity;

            if (taskOptions.storageOptions != null) {

                options.storageOptions = new ProjectStorageOptions();
                options.storageOptions.directory =
                        taskOptions.storageOptions.directory;
                options.storageOptions.files =
                        taskOptions.storageOptions.files;
                options.storageOptions.newversion =
                        taskOptions.storageOptions.newversion;
                options.storageOptions.objects =
                        taskOptions.storageOptions.objects;
                options.storageOptions.published =
                        taskOptions.storageOptions.published;
                options.storageOptions.workspace =
                        taskOptions.storageOptions.workspace;
            }
        }

        return options;
    }

    public static JobExecutionOptions translate(
            BackgroundTaskOptions taskOptions,
            boolean isPriorityTask) {

        JobExecutionOptions options = null;

        if (taskOptions != null) {

            options = new JobExecutionOptions();

            /*
             * BackgroundTaskOptions to JobExecutionOptions.
             */

            if (isPriorityTask)
                options.priority = JobExecutionOptions.HIGH_PRIORITY;

            options.noproject = taskOptions.noproject;

            if (taskOptions.repeatCount > 0) {
                options.schedulingOptions = new JobSchedulingOptions();
                options.schedulingOptions.repeatCount =
                        taskOptions.repeatCount;
                options.schedulingOptions.repeatInterval =
                        taskOptions.repeatInterval;
                options.schedulingOptions.startTime =
                        taskOptions.startTime;
            }

            /*
             * BackgroundTaskOptions to ProjectExecutionOptions.
             */

            options.rinputs = taskOptions.rinputs;
            options.csvrinputs = taskOptions.csvrinputs;

            if (taskOptions.preloadWorkspace != null) {

                options.preloadWorkspace = new ProjectPreloadOptions();
                options.preloadWorkspace.filename =
                        taskOptions.preloadWorkspace.filename;
                options.preloadWorkspace.directory =
                        taskOptions.preloadWorkspace.directory;
                options.preloadWorkspace.author =
                        taskOptions.preloadWorkspace.author;
                options.preloadWorkspace.version =
                        taskOptions.preloadWorkspace.version;
            }

            if (taskOptions.preloadDirectory != null) {

                options.preloadDirectory = new ProjectPreloadOptions();
                options.preloadDirectory.filename =
                        taskOptions.preloadDirectory.filename;
                options.preloadDirectory.directory =
                        taskOptions.preloadDirectory.directory;
                options.preloadDirectory.author =
                        taskOptions.preloadDirectory.author;
                options.preloadDirectory.version =
                        taskOptions.preloadDirectory.version;
            }

            options.preloadByDirectory = taskOptions.preloadByDirectory;

            options.graphicsDevice = taskOptions.graphicsDevice;
            options.graphicsWidth = taskOptions.graphicsWidth;
            options.graphicsHeight = taskOptions.graphicsHeight;
            options.echooff = taskOptions.echooff;
            options.consoleoff = taskOptions.consoleoff;
            options.artifactsoff = taskOptions.artifactsoff;
            options.routputs = taskOptions.routputs;
            options.encodeDataFramePrimitiveAsVector =
                    taskOptions.encodeDataFramePrimitiveAsVector;
            options.nan = taskOptions.nan;
            options.infinity = taskOptions.infinity;

            if (taskOptions.storageOptions != null) {

                options.storageOptions = new ProjectStorageOptions();
                options.storageOptions.directory =
                        taskOptions.storageOptions.directory;
                options.storageOptions.files =
                        taskOptions.storageOptions.files;
                options.storageOptions.newversion =
                        taskOptions.storageOptions.newversion;
                options.storageOptions.objects =
                        taskOptions.storageOptions.objects;
                options.storageOptions.published =
                        taskOptions.storageOptions.published;
                options.storageOptions.workspace =
                        taskOptions.storageOptions.workspace;
            }

        }

        return options;
    }

    public static ProjectCreationOptions translate(
            PoolCreationOptions poolOptions) {

        ProjectCreationOptions options = null;

        if (poolOptions != null) {

            options = new ProjectCreationOptions();

            /*
             * PoolCreationOptions to ProjectCreationOptions.
             */

            options.rinputs = poolOptions.rinputs;

            if (poolOptions.preloadWorkspace != null) {

                options.preloadWorkspace = new ProjectPreloadOptions();
                options.preloadWorkspace.filename =
                        poolOptions.preloadWorkspace.filename;
                options.preloadWorkspace.directory =
                        poolOptions.preloadWorkspace.directory;
                options.preloadWorkspace.author =
                        poolOptions.preloadWorkspace.author;
                options.preloadWorkspace.version =
                        poolOptions.preloadWorkspace.version;
            }

            if (poolOptions.preloadDirectory != null) {

                options.preloadDirectory = new ProjectPreloadOptions();
                options.preloadDirectory.filename =
                        poolOptions.preloadDirectory.filename;
                options.preloadDirectory.directory =
                        poolOptions.preloadDirectory.directory;
                options.preloadDirectory.author =
                        poolOptions.preloadDirectory.author;
                options.preloadDirectory.version =
                        poolOptions.preloadDirectory.version;
            }

            options.preloadByDirectory = poolOptions.preloadByDirectory;

        }

        return options;
    }

    public static ProjectExecutionOptions migrate(
            PoolCreationOptions poolOptions) {

        ProjectExecutionOptions options = null;

        if (poolOptions != null) {

            options = new ProjectExecutionOptions();

            /*
             * PoolCreationOptions to ProjectExecutionOptions.
             */

            options.rinputs = poolOptions.rinputs;

            if (poolOptions.preloadWorkspace != null) {

                options.preloadWorkspace = new ProjectPreloadOptions();
                options.preloadWorkspace.filename =
                        poolOptions.preloadWorkspace.filename;
                options.preloadWorkspace.directory =
                        poolOptions.preloadWorkspace.directory;
                options.preloadWorkspace.author =
                        poolOptions.preloadWorkspace.author;
                options.preloadWorkspace.version =
                        poolOptions.preloadWorkspace.version;
            }

            if (poolOptions.preloadDirectory != null) {

                options.preloadDirectory = new ProjectPreloadOptions();
                options.preloadDirectory.filename =
                        poolOptions.preloadDirectory.filename;
                options.preloadDirectory.directory =
                        poolOptions.preloadDirectory.directory;
                options.preloadDirectory.author =
                        poolOptions.preloadDirectory.author;
                options.preloadDirectory.version =
                        poolOptions.preloadDirectory.version;
            }

            options.preloadByDirectory = poolOptions.preloadByDirectory;

        }

        return options;
    }
}
