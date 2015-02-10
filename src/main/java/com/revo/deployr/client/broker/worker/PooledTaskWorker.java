/*
 * PooledTaskWorker.java
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
package com.revo.deployr.client.broker.worker;

import com.revo.deployr.client.*;
import com.revo.deployr.client.broker.RBroker;
import com.revo.deployr.client.broker.RTaskResult;
import com.revo.deployr.client.broker.RTaskType;
import com.revo.deployr.client.broker.engine.PooledTaskBroker;
import com.revo.deployr.client.broker.impl.RTaskResultImpl;
import com.revo.deployr.client.broker.impl.util.ROptionsTranslator;
import com.revo.deployr.client.broker.task.PooledTask;
import com.revo.deployr.client.data.RData;
import com.revo.deployr.client.params.ProjectExecutionOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PooledTaskWorker implements RBrokerWorker {

    private final PooledTask task;
    private final long executorTaskRef;
    private final boolean isPriorityTask;
    private final RProject rProject;
    private final PooledTaskBroker rBroker;

    public PooledTaskWorker(PooledTask task,
                            long executorTaskRef,
                            boolean isPriorityTask,
                            RProject resourceToken,
                            RBroker rBroker) {

        this.task = task;
        this.executorTaskRef = executorTaskRef;
        this.isPriorityTask = isPriorityTask;
        this.rProject = resourceToken;
        this.rBroker = (PooledTaskBroker) rBroker;
    }

    public RTaskResult call() throws RClientException,
            RSecurityException,
            RDataException,
            RGridException {

        RTaskResult taskResult = null;

        long timeOnCall = 0L;
        long timeOnServer = 0L;

        try {

            ProjectExecutionOptions options =
                    ROptionsTranslator.translate(task.options);
            /*
             * Flag pooled task execution as phantom execution
             * to minimize server-side database resource usage.
             */
            options.phantom = true;

            long startTime = System.currentTimeMillis();

            RProjectExecution execResult = null;

            if (task.code != null) {
                execResult = rProject.executeCode(task.code,
                        options);
            } else if (task.external != null) {
                execResult = rProject.executeExternal(task.external,
                        options);
            } else {
                execResult = rProject.executeScript(task.filename,
                        task.directory,
                        task.author,
                        task.version,
                        options);
            }


            timeOnCall = System.currentTimeMillis() - startTime;

            String generatedConsole = execResult.about().console;

            List<URL> generatedPlots = new ArrayList<URL>();
            if (execResult.about().results != null) {
                for (RProjectResult result : execResult.about().results) {
                    generatedPlots.add(result.about().url);
                }
            }

            List<URL> generatedFiles = new ArrayList<URL>();
            if (execResult.about().artifacts != null) {
                for (RProjectFile artifact : execResult.about().artifacts) {
                    generatedFiles.add(artifact.download());
                }
            }

            List<RData> generatedObjects = execResult.about().workspaceObjects;

            List<URL> storedFiles = new ArrayList<URL>();
            if (execResult.about().repositoryFiles != null) {
                for (RRepositoryFile repoFile : execResult.about().repositoryFiles) {
                    storedFiles.add(repoFile.download());
                }
            }

            taskResult = new RTaskResultImpl(execResult.about().id,
                    RTaskType.POOLED,
                    true,
                    execResult.about().timeCode,
                    execResult.about().timeTotal,
                    timeOnCall, null,
                    false,
                    generatedConsole,
                    generatedPlots,
                    generatedFiles,
                    generatedObjects,
                    storedFiles);

        } catch (Exception ex) {

            if (ex.getCause() instanceof InterruptedException) {
                try {
                    /*
                     * If RTaskToken.cancel() raises InterruptedException
                     * then ensure any corresponding execution on RProject is
                     * also cancelled.
                     */
                    rProject.interruptExecution();
                } catch (Exception iex) {
                }
            }

            taskResult = new RTaskResultImpl(null,
                    RTaskType.POOLED,
                    false,
                    0L,
                    0L,
                    0L, ex);
        } finally {

            /*
             * Callback to PooledTaskBroker to release
             * RProject back into pool for other tasks.
             */
            rBroker.callback(task, taskResult);
        }

        return taskResult;
    }
}
