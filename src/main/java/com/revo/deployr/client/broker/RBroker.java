/*
 * RBroker.java
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
package com.revo.deployr.client.broker;

import com.revo.deployr.client.RUser;
import com.revo.deployr.client.broker.app.RTaskAppSimulator;
import com.revo.deployr.client.broker.config.RBrokerConfig;

/**
 * <p>
 * Represents a high-level programming model for
 * building DeployR-enabled client applications.
 * By using {@link com.revo.deployr.client.broker.RBroker} an
 * application developer can focus entirely on integrating
 * R Analytics, while offloading the complexity
 * of managing client-side API task queues and server-side
 * R session lifecycles.
 * </p>
 * <p>
 * The basic programming model for working with
 * {@link com.revo.deployr.client.broker.RBroker} is as follows:
 * </p> 
 * <ol>
 * <li>Decide if the R Analytics tasks for your application
 * should execute as:
 * <ul>
 * <li><b>Discrete tasks:</b> authentication optional, grid resources
 * allocated at runtime, results returned immediately, no persistence.
 * Good for prototyping and public facing production deployments. 
 * <li><b>Pooled tasks:</b> authentication required, grid resources
 * pre-allocated, results returned immediately, optional persistence
 * to repository. Good for enterprise production deployments,
 * consistent runtime, high-throughput environments.
 * <li><b>Background tasks:</b> authentication required, grid resources
 * allocated at runtime, results persisted for later retrieval. Good
 * for periodic, scheduled or batch processing.
 * </ul>
 * <li>Use the {@link com.revo.deployr.client.factory.RBrokerFactory}
 * to create an appropriate instance of
 * {@link com.revo.deployr.client.broker.RBroker}.
 * <li>Define the R Analytics tasks for your application as one
 * or more {@link com.revo.deployr.client.broker.RTask}.
 * <li>Submit your {@link com.revo.deployr.client.broker.RTask} to
 * {@link com.revo.deployr.client.broker.RBroker} for execution.
 * <li>Track the progress of your {@link com.revo.deployr.client.broker.RTask}
 * using {@link com.revo.deployr.client.broker.RTaskToken}.
 * <li>Integrate the results of your {@link com.revo.deployr.client.broker.RTask}
 * found within {@link com.revo.deployr.client.broker.RTaskResult}.
 * </ol>
 * <p>
 * This programming model can be further simplified for application
 * developers by leveraging asynchronous callbacks. Simply register
 * an {@link com.revo.deployr.client.broker.RTaskListener} with your
 * RBroker instance and the RBroker will automatically notify your
 * application when each
 * {@link com.revo.deployr.client.broker.RTask} completes.
 * This approach allows your application to skip step 5. above
 * and scale effortlessly.
 * </p>
 */
public interface RBroker {

    /**
     * <p>
     * Refresh configuration for
     * {@link com.revo.deployr.client.broker.RBroker}.
     * </p>
     * <p>
     * Note, support for refresh is only available on the
     * PooledTaskBroker runtime. In addition, only
     * {@link com.revo.deployr.client.broker.options.PoolCreationOptions}
     * are processed on this call. All other RBrokerConfig options are
     * ignored.
     * </p>
     * <p>
     * A refresh causes all workspace objects and directory files
     * in the underlying R sessions within the pool to be
     * cleared before new workspace objects and/or directory
     * files are loaded per the new config options.
     * </p>
     * Only an idle {@link com.revo.deployr.client.broker.RBroker}
     * instance can be refreshed. An
     * {@link com.revo.deployr.client.broker.RBrokerException}
     * will be raised if this method is called when the instance
     * of {@link com.revo.deployr.client.broker.RBroker}
     * is currently busy with queued or executing
     * {@link com.revo.deployr.client.broker.RTask}.
     */
    public void refresh(RBrokerConfig config)
            throws RBrokerException;

    /**
     * Submit an {@link com.revo.deployr.client.broker.RTask}
     * for execution under the control of
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public RTaskToken submit(RTask task)
            throws RBrokerException;

    /**
     * <p>
     * Submit a priority {@link com.revo.deployr.client.broker.RTask}
     * for execution under the control of
     * {@link com.revo.deployr.client.broker.RBroker}.
     * </p>
     * Priority tasks are automatically moved to the front of the
     * queue, ahead of all standard tasks that are already pending
     * execution by the broker.
     */
    public RTaskToken submit(RTask task, boolean priority)
            throws RBrokerException;

    /**
     * Register an asynchronous listener to receive callbacks
     * on {@link com.revo.deployr.client.broker.RTask} completion
     * or failure events.
     */
    public void addTaskListener(RTaskListener taskListener)
            throws RBrokerException;

    /**
     * Register an asynchronous listener to receive callbacks
     * on {@link com.revo.deployr.client.broker.RBroker}
     * runtime statistics events or runtime errors.
     */
    public void addBrokerListener(RBrokerListener brokerListener)
            throws RBrokerException;

    /**
     * <p>
     * Launch an RTaskAppSimulator simulation. The
     * {@link com.revo.deployr.client.broker.RTask} defined
     * by your simulation will be automatically executed by
     * the current instance of
     * {@link com.revo.deployr.client.broker.RBroker}.
     * </p>
     * Make sure to register your
     * {@link com.revo.deployr.client.broker.RTaskListener}
     * and  {@link com.revo.deployr.client.broker.RBrokerListener}
     * before starting your simulation in order to receive
     * asynchronous callbacks in your application when
     * {@link com.revo.deployr.client.broker.RTask} complete
     * and/or to receive runtime summary statistics from
     * {@link com.revo.deployr.client.broker.RBroker} as the
     * simulation proceeds.
     */
    public void simulateApp(RTaskAppSimulator appSimulator);

    /**
     * Returns the task execution concurrency levels enforced for
     * this instance of {@link com.revo.deployr.client.broker.RBroker}.
     */
    public int maxConcurrency();

    /**
     * <p>
     * Returns status indicating current
     * {@link com.revo.deployr.client.broker.RTask}
     * activity on {@link com.revo.deployr.client.broker.RBroker}.
     * </p>
     * <p>
     * This call can be used to determine if an RBroker instance is
     * idle which can be particularly useful ahead calls to
     * {@link com.revo.deployr.client.broker.RBroker#shutdown}.
     * </p>
     * <p>
     * The {@link com.revo.deployr.client.broker.RBrokerStatus#pendingTasks}
     * and {@link com.revo.deployr.client.broker.RBrokerStatus#executingTasks}
     * fields can be used by an application to estimate time remaining
     * until {@link com.revo.deployr.client.broker.RBroker} reaches an idle
     * state.
     * </p>
     * The {@link com.revo.deployr.client.broker.RBrokerStatus#isIdle} field
     * is provided for convenience for an application if individual pending
     * and executing task counts are not relevant in
     * {@link com.revo.deployr.client.broker.RBroker#shutdown} decisions.
     */
    public RBrokerStatus status();

    /**
     * Flushes all pending
     * {@link com.revo.deployr.client.broker.RTask}
     * from queues maintained by
     * {@link com.revo.deployr.client.broker.RBroker}.
     * Flushing {@link com.revo.deployr.client.broker.RTask}
     * queues ensures that queued tasks will not be executed by
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public RBrokerStatus flush();

    /**
     * Indicates if current {@link com.revo.deployr.client.broker.RBroker}
     * instance is still connected to the DeployR server. A connection may
     * be lost for a number of reasons, for example, due to a droppeed
     * network connection between client and server or if the DeployR server
     * itself goes down.
     */
    public boolean isConnected();

    /**
     * Release all client-side and server-side resources maintained
     * by or on behalf of an instance of
     * {@link com.revo.deployr.client.broker.RBroker}.
     */
    public void shutdown();

    /**
     * <p>
     * Returns a token indicating the owner of the current instance of
     * {@link com.revo.deployr.client.broker.RBroker}.
     * </p>
     * For anonymous {@link com.revo.deployr.client.broker.RBroker}
     * instances, null is returned. For authenticated
     * {@link com.revo.deployr.client.broker.RBroker} instances,
     * an instance of com.revo.deployr.client.RUser is returned.
     */
    public RUser owner();
}
