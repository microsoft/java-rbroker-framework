/*
 * DiscreteTaskWorker.java
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
import com.revo.deployr.client.broker.engine.DiscreteTaskBroker;
import com.revo.deployr.client.broker.impl.RTaskResultImpl;
import com.revo.deployr.client.broker.impl.util.ROptionsTranslator;
import com.revo.deployr.client.broker.task.DiscreteTask;
import com.revo.deployr.client.data.RData;
import com.revo.deployr.client.params.AnonymousProjectExecutionOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscreteTaskWorker implements RBrokerWorker {

    private final DiscreteTask task;
    private final long executorTaskRef;
    private final boolean isPriorityTask;
    private final RClient rClient;
    private final Integer resourceToken;
    private final DiscreteTaskBroker rBroker;

    public DiscreteTaskWorker(DiscreteTask task,
                              long executorTaskRef,
                              boolean isPriorityTask,
                              RClient rClient,
                              Integer resourceToken,
                              RBroker rBroker) {

        this.task = task;
        this.executorTaskRef = executorTaskRef;
        this.isPriorityTask = isPriorityTask;
        this.rClient = rClient;
        this.resourceToken = resourceToken;
        this.rBroker = (DiscreteTaskBroker) rBroker;
    }

    public RTaskResult call() throws RClientException,
            RSecurityException,
            RDataException,
            RGridException {

        RTaskResult taskResult = null;

        long timeOnCall = 0L;
        long timeOnServer = 0L;

        try {

            AnonymousProjectExecutionOptions options =
                    ROptionsTranslator.translate(task.options);

            long startTime = System.currentTimeMillis();

            RScriptExecution execResult = null;

            if (task.external != null) {
                execResult = rClient.executeExternal(task.external,
                        options);
            } else {
                execResult = rClient.executeScript(task.filename,
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
                    // generatedFiles.add(artifact.download());
                    generatedFiles.add(artifact.about().url);
                }
            }

            List<RData> generatedObjects = execResult.about().workspaceObjects;

            List<URL> storedFiles = new ArrayList<URL>();
            if (execResult.about().repositoryFiles != null) {
                for (RRepositoryFile repoFile : execResult.about().repositoryFiles) {
                    // storedFiles.add(repoFile.download());
                    storedFiles.add(repoFile.about().url);
                }
            }

            taskResult = new RTaskResultImpl(execResult.about().id,
                    RTaskType.DISCRETE,
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
                /*
                 * RTaskToken.cancel() can raise an InterruptedException.
                 * When an InterruptedException is detected the DiscreteTask
                 * executing on the server should be aborted at this point.
                 * However, there is no way to obtain DeployR reference, such
                 * as a projectId, for an stateless execution in-progress, so
                 * aborting the current RTask operation is not possible.
                 */
            }

            taskResult = new RTaskResultImpl(null,
                    RTaskType.DISCRETE,
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
