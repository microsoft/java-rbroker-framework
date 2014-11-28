/*
 * BackgroundTaskWorker.java
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
import com.revo.deployr.client.broker.engine.BackgroundTaskBroker;
import com.revo.deployr.client.broker.impl.RTaskResultImpl;
import com.revo.deployr.client.broker.impl.util.ROptionsTranslator;
import com.revo.deployr.client.broker.task.BackgroundTask;
import com.revo.deployr.client.params.JobExecutionOptions;

public class BackgroundTaskWorker implements RBrokerWorker {

    private final BackgroundTask task;
    private final long executorTaskRef;
    private final boolean isPriorityTask;
    private final RUser rUser;
    private final Integer resourceToken;
    private final BackgroundTaskBroker rBroker;

    public BackgroundTaskWorker(BackgroundTask task,
                                long executorTaskRef,
                                boolean isPriorityTask,
                                RUser rUser,
                                Integer resourceToken,
                                RBroker rBroker) {

        this.task = task;
        this.executorTaskRef = executorTaskRef;
        this.isPriorityTask = isPriorityTask;
        this.rUser = rUser;
        this.resourceToken = resourceToken;
        this.rBroker = (BackgroundTaskBroker) rBroker;

    }

    public RTaskResult call() throws RClientException,
            RSecurityException,
            RDataException {


        RTaskResult taskResult = null;
        RJob rJob = null;

        long timeOnCall = 0L;
        long timeOnServer = 0L;

        try {

            long startTime = System.currentTimeMillis();


            JobExecutionOptions options =
                    ROptionsTranslator.translate(task.options,
                            isPriorityTask);

            if (task.code != null) {
                rJob = rUser.submitJobCode(task.name,
                        task.description,
                        task.code,
                        options);
            } else if (task.external != null) {
                rJob = rUser.submitJobExternal(task.external,
                        task.description,
                        task.code,
                        options);
            } else {

                rJob = rUser.submitJobScript(task.name,
                        task.description,
                        task.filename,
                        task.directory,
                        task.author,
                        task.version,
                        options);
            }

            timeOnCall = System.currentTimeMillis() - startTime;

            taskResult = new RTaskResultImpl(rJob.about().id,
                    RTaskType.BACKGROUND,
                    true,
                    0L,
                    0L,
                    timeOnCall,
                    null);

        } catch (Exception ex) {

            if (ex.getCause() instanceof InterruptedException) {
                try {
                    /*
                     * If RTaskToken.cancel() call raises InterruptedException
                     * then ensure any corresponding scheduled RJob is
                     * also cancelled.
                     */
                    rJob.cancel();
                } catch (Exception iex) {
                }
            }

            taskResult = new RTaskResultImpl(null,
                    RTaskType.BACKGROUND,
                    false,
                    0L,
                    0L,
                    0L,
                    ex);
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
