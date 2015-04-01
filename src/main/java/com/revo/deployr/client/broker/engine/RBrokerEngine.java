/*
 * RBrokerEngine.java
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
package com.revo.deployr.client.broker.engine;

import com.revo.deployr.client.*;
import com.revo.deployr.client.broker.*;
import com.revo.deployr.client.broker.app.RTaskAppSimulator;
import com.revo.deployr.client.broker.config.PooledBrokerConfig;
import com.revo.deployr.client.broker.config.RBrokerConfig;
import com.revo.deployr.client.broker.impl.RTaskResultImpl;
import com.revo.deployr.client.broker.impl.RTaskTokenImpl;
import com.revo.deployr.client.broker.impl.RTaskTokenListener;
import com.revo.deployr.client.broker.worker.RBrokerWorker;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.security.*;
import javax.net.ssl.*;

/*
 * RBrokerEngine
 */
public abstract class RBrokerEngine implements RBroker {

    protected final Semaphore engineInitialized = new Semaphore(0);
    /*
     * Thread per manager:
     * RBrokerWorkerManager, RBrokerListenerManager and one
     * additional thread available to concrete implementation
     * that may require custom Manager.
     */
    protected final ExecutorService brokerEngineExecutor =
            Executors.newFixedThreadPool(3);

    protected final ArrayBlockingQueue<RTask> pendingLowPriorityQueue =
            new ArrayBlockingQueue<RTask>(MAX_TASK_QUEUE_SIZE);

    protected final ArrayBlockingQueue<RTask> pendingHighPriorityQueue =
            new ArrayBlockingQueue<RTask>(MAX_TASK_QUEUE_SIZE);

    private ConcurrentLinkedQueue<RTaskToken> liveTaskTokens =
            new ConcurrentLinkedQueue<RTaskToken>();

    /*
     * Asynchronous RTask and RBroker listeners.
     */
    protected RTaskListener taskListener;
    protected RBrokerListener brokerListener;

    /*
     * RTask Application Simulator.
     */
    private RTaskAppSimulator appSimulator;

    /*
     * taskBrokerIsActive signals RBroker[*]Manager thread
     * exit. Flag disabled on call to RBroker.shutdown().
     */
    protected final AtomicBoolean taskBrokerIsActive = new AtomicBoolean(true);
    protected final AtomicLong executorTaskCounter = new AtomicLong(0L);

    /*
     * refreshingConfig when enabled prevents further RTask
     * submissions until flag is cleared.
     */
    protected final AtomicBoolean refreshingConfig = new AtomicBoolean(false);

    protected RBrokerConfig brokerConfig;
    protected AtomicInteger parallelTaskLimit;
    protected RClient rClient;
    protected RUser rUser;
    protected ExecutorService taskWorkerExecutor;
    protected static int MAX_TASK_QUEUE_SIZE = 99999;
    public long LIVE_TASK_TOKEN_PEEK_INTERVAL = 25L;
    protected ConcurrentHashMap<RTask, Object> taskResourceTokenMap;
    protected ConcurrentHashMap<RTask, RTaskTokenListener> taskTokenListenerMap;

    /*
     * For an RTask to execute, it most hold a resourceToken
     * taken from the resourceTokenPool. The size of the
     * resourceTokenPool determines the maximum number of
     * concurrent RTask that can be executing at the same time.
     *
     * The nature of the resourceToken itself will depend on the
     * concrete implementation of the RBrokerEngine. Currently,
     * the DiscteteTaskBroker and BackgroundTaskBroker use simple
     * java.lang.Object as resourceTokens. The PooledTaskBroker
     * uses com.revo.deployr.client.RProject.
     *
     * When an RTask completes the RBrokerWorker that executed
     * the task is responsible for making a callback() on
     * RBrokerEngine signaling that the resourceToken associated
     * with the RTask can be released back into the resourceTokenPool,
     * making the token available for use by another RTask to run.
     */
    protected ArrayBlockingQueue<Object> resourceTokenPool;

    public RBrokerEngine(RBrokerConfig brokerConfig) throws RBrokerException {

        this.brokerConfig = brokerConfig;

        /*
         * Validate DeployR server endpoint passed to RBroker.
         */
        try {
            /*
             * Test the /r/server/info endpoint, expect HTTP 200.
             */
            String serverInfoEndpoint = brokerConfig.deployrEndpoint +
                                        "/r/server/info?format=json";
            URLConnection urlConn =
                (new URL(serverInfoEndpoint)).openConnection();
            HttpsURLConnection trustedConn = null;

            if(brokerConfig.allowSelfSignedSSLCert &&
                    urlConn instanceof HttpsURLConnection) {

                /*
                 * Build a temporary TrustManager for this
                 * /r/server/info call that accepts self-signed SSL
                 * certificates for the purposes of this endpoint
                 * validation call.
                 */

                TrustManager[] selfTrustManager = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                            return null;
                        } 
                        public void checkClientTrusted( 
                            java.security.cert.X509Certificate[] certs, String authType) {
                        } 
                        public void checkServerTrusted( 
                            java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
                };

                /*
                 * Install the custom TrustManager.
                 */
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, selfTrustManager, new SecureRandom());
                SSLSocketFactory selfTrustSocketFactory = sc.getSocketFactory();
                HostnameVerifier selfTrustHostnameVerifier = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                trustedConn = (HttpsURLConnection) urlConn;
                trustedConn.setSSLSocketFactory(selfTrustSocketFactory);
                trustedConn.setHostnameVerifier(selfTrustHostnameVerifier);

            }

            /*
             * Make endpoint connection, catch handles failure.
             */

            InputStream is = null;
            try {
                if(trustedConn != null)
                    is = trustedConn.getInputStream();
                else
                    is = urlConn.getInputStream();
            } finally {
                if(is != null) {
                    try {
                       is.close();
                    } catch(java.io.IOException ioex) {}
                }
            }

        } catch(Exception ex) {
            /*
             * Halt RBroker instance initialization,
             * report invalid DeployR server endpoint.
             */
           throw new RBrokerException("DeployR endpoint invalid=" +
                                                ex.getMessage(), ex);
        }
    }

    protected void initEngine(int parallelTaskLimit)
            throws RBrokerException {

        try {

            this.parallelTaskLimit = new AtomicInteger(parallelTaskLimit);

            this.taskWorkerExecutor =
                    Executors.newFixedThreadPool(parallelTaskLimit);

            this.resourceTokenPool =
                    new ArrayBlockingQueue<Object>(parallelTaskLimit);

            this.taskResourceTokenMap =
                    new ConcurrentHashMap<RTask, Object>(parallelTaskLimit);

            this.taskTokenListenerMap =
                    new ConcurrentHashMap<RTask, RTaskTokenListener>(parallelTaskLimit);

        } catch (Exception ex) {
            throw new RBrokerException("Broker failed to " +
                    "initialize, cause" + ex);
        }

        try {
            brokerEngineExecutor.execute(new RBrokerWorkerManager());
        } catch (RejectedExecutionException rex) {
            shutdown();
            throw new RBrokerException("Broker failed " +
                    "to start worker manager, cause: " + rex);
        }

        try {
            brokerEngineExecutor.execute(new RBrokerListenerManager());
        } catch (RejectedExecutionException rex) {
            shutdown();
            throw new RBrokerException("Broker failed " +
                    "to start listener manager, cause: " + rex);
        }

        try {
            engineInitialized.tryAcquire(5000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException iex) {
            shutdown();
            throw new RBrokerException("Broker failed " +
                    "to initialized, cause: " + iex);
        }
    }

    /*
     * RBroker Interface implementation.
     */

    public RTaskToken submit(RTask task)
            throws RBrokerException,
            IllegalStateException,
            UnsupportedOperationException {
        return submit(task, false);
    }

    public RTaskToken submit(RTask task, boolean priority)
            throws RBrokerException {

        if (refreshingConfig.get()) {
            throw new RBrokerException("RTask submissions temporarily " +
                    "disabled while RBroker configuration refreshes.");
        }

        try {

            /*
             * We clone the incoming RTask into a new instance of
             * RTask to ensure RTask is unique so it can be used
             * as a key inside the taskTokenListenerMap.
             *
             * How could the value of RTask param not be unique?
             *
             * For example, RBrokerAppSimulator apps frequently
             * create a single RTask instance and submit it many times
             * to simulate load.
             */
            RTask clonedTask = cloneTask(task);

            /*
             * Prepare setup for RTaskToken.
             */
            RTaskToken rTaskToken = new RTaskTokenImpl(task);
            /*
             * Appending associated RTaskToken to end of
             * liveTaskTokens in preparation for pushing
             * RTask onto pending[High,Low]PriorityQueue.
             */
            liveTaskTokens.add(rTaskToken);

            /*
             * Register RTask and associated RTaskToken here.
             * Once RTask has been submitted to Executor and
             * Future for task exists, we will make an
             * RTaskToken.onTask callback to register Future
             * on token.
             */
            taskTokenListenerMap.put(clonedTask, rTaskToken);

            boolean added = false;
            if (priority)
                added = pendingHighPriorityQueue.offer(clonedTask);
            else
                added = pendingLowPriorityQueue.offer(clonedTask);

            if (!added) {
                
                /*
                 * Undo (above) setup for RTaskToken that was
                 * rejected by pending[High,Low]PriorityQueues.
                 */
                boolean liveTaskTokenRemoved =
                    liveTaskTokens.remove(rTaskToken);
                Object clonedTaskRemoved =
                    taskTokenListenerMap.remove(clonedTask);

                if(!liveTaskTokenRemoved || clonedTaskRemoved == null) {
                    System.out.println("RBrokerEngine: " +
                        "Broker at capacity, rTask rejected, cleanup: " +
                        liveTaskTokenRemoved + ", " + clonedTaskRemoved);
                }

                throw new RBrokerException("Broker at capacity ( " +
                        MAX_TASK_QUEUE_SIZE + " ), rejecting task " + clonedTask);
            }

            return rTaskToken;

        } catch (Exception rex) {
            throw new RBrokerException("RBroker: " +
                    "submit failed, cause: " + rex.getMessage(), rex);
        }

    }

    public void addTaskListener(RTaskListener taskListener)
            throws RBrokerException {

        this.taskListener = taskListener;
    }

    public void addBrokerListener(RBrokerListener brokerListener)
            throws RBrokerException {

        this.brokerListener = brokerListener;
    }

    public void simulateApp(RTaskAppSimulator appSimulator) {

        /*
         * Auto-register RTaskAppSimulator as RTaskListener
         * if interface is implemented by appSimulator.
         */
        if (taskListener == null &&
                (appSimulator instanceof RTaskListener)) {
            this.taskListener = (RTaskListener) appSimulator;
        }

        /*
         * Auto-register RTaskAppSimulator as RBrokerListener
         * if interface is implemented by appSimulator.
         */
        if (brokerListener == null &&
                (appSimulator instanceof RBrokerListener)) {
            this.brokerListener = (RBrokerListener) appSimulator;
        }

        this.appSimulator = appSimulator;

        if (appSimulator != null) {
            appSimulator.simulateApp(this);
        }

    }

    public int maxConcurrency() {
        return this.parallelTaskLimit.get();
    }

    public RBrokerStatus status() {

        /*
         * Pending tasks include all tasks on
         * high and low priority queues.
         */
        int pendingTasks = pendingHighPriorityQueue.size() +
                pendingLowPriorityQueue.size();
        int executingTasks =
                parallelTaskLimit.get() - resourceTokenPool.size();

        return new RBrokerStatus(pendingTasks,
                executingTasks);
    }

    public RBrokerStatus flush() {

        /*
         * Flush all pending tasks from
         * high and low priority queues.
         */
        pendingHighPriorityQueue.clear();
        pendingLowPriorityQueue.clear();
        return status();
    }

    public boolean isConnected() {

        boolean connected = false;

        if (rUser != null) {
            /*
             * Test connection to authenticated
             * HTTP session on server.
             */
            try {
                rUser.autosaveProjects(false);
                connected = true;
            } catch (Exception ex) {
            }

        } else {
            /*
             * Test connection to server.
             */
            URL dURL = null;
            InputStream dIn = null;
            try {
                dURL = new URL(brokerConfig.deployrEndpoint);
                URLConnection dConn = dURL.openConnection();
                dIn = dConn.getInputStream();
                connected = true;
            } catch (Exception ex) {
            } finally {
                if (dIn != null) {
                    try {
                        dIn.close();
                    } catch (Exception cex) {
                    }
                }
            }
        }
        return connected;
    }

    @Override
    public void shutdown() {

        taskBrokerIsActive.set(false);

        try {

            if (resourceTokenPool != null) {

                Object resourceToken = resourceTokenPool.peek();
                if (resourceToken instanceof RProject) {

                    PooledBrokerConfig pbcfg =
                            (PooledBrokerConfig) brokerConfig;
                    boolean releaseGridResources =
                            pbcfg.poolCreationOptions.releaseGridResources;
                    if (releaseGridResources) {
                        /*
                         * If PooledTaskBroker resource tokens
                         * and rUser available, perform a server-wide
                         * flush of projects on the grid.
                         */
                        rUser.releaseProjects();
                    } else {
                        /*
                         * If PooledTaskBroker resource tokens
                         * and rUser not available, perform
                         * project-by-project flush on the grid.
                         */
                        for (Object resToken : resourceTokenPool) {
                            if (resToken instanceof RProject) {
                                RProject projectToken =
                                        (RProject) resourceToken;
                                try {
                                    projectToken.close();
                                } catch (Exception pex) {
                                }
                            }
                        }
                    }
                }

                resourceTokenPool = null;
            }
        } catch (Exception cex) {
        }

        if (rClient != null) {
            try {
                rClient.release();
            } catch (Exception rex) {
            }
        }

        try {
            brokerEngineExecutor.shutdownNow();
        } catch (Exception bex) {
        }
        try {
            taskWorkerExecutor.shutdownNow();
        } catch (Exception tex) {
        }
    }

    public RUser owner() {
        return rUser;
    }

    /*
     * Callback for RBrokerWorker. Must be implemented by
     * concrete implementation of RBrokerEngine.
     *
     * Responsible for adding an engineResourceToken
     * back into the RBrokerEngine resourceTokenPool, 
     * making the token available for use by another
     * RTask.
     */
    public abstract void callback(RTask task, RTaskResult result);

    protected abstract RTask cloneTask(RTask genesis);

    protected abstract RBrokerWorker createBrokerWorker(RTask task,
                                                        long taskIndex,
                                                        boolean isPriorityTask,
                                                        Object resourceToken,
                                                        RBrokerEngine brokerEngine);

    /*
     * RBrokerEngine: private implementation.
     */
    private class RBrokerWorkerManager implements Runnable {

        public void run() {

            try {

                /*
                 * Signal to constructor that the brokerEngineExecutor
                 * thread is fully initialized and active, allowing
                 * the constructor to return safely to caller.
                 */
                engineInitialized.release();

                while (taskBrokerIsActive.get()) {

                    RTask nextTaskInQueue = null;

                    /*
                     * Await next queued Task.
                     */
                    boolean priorityTaskAvailable = false;
                    while (nextTaskInQueue == null &&
                            taskBrokerIsActive.get()) {

                        /*
                         * Retrieves but does not remove the task
                         * at the head of the queue.
                         */

                        nextTaskInQueue = pendingHighPriorityQueue.peek();
                        if (nextTaskInQueue == null) {
                            nextTaskInQueue = pendingLowPriorityQueue.peek();
                            priorityTaskAvailable = false;
                        } else {
                            priorityTaskAvailable = true;
                        }

                        if (nextTaskInQueue == null) {
                            try {
                                /*
                                 * Avoid busy-wait, sleep.
                                 */
                                Thread.currentThread().sleep(50);
                            } catch (Exception tex) {
                            }
                        } else {
                            /*
                             * Retrieves and removes the task
                             * at the head of the queue.
                             */
                            nextTaskInQueue = priorityTaskAvailable ?
                                    pendingHighPriorityQueue.take() :
                                    pendingLowPriorityQueue.take();
                        }
                    }

                    /*
                     * If task found on queue and taskBroker
                     * is still active, process task.
                     */
                    if (nextTaskInQueue != null &&
                            taskBrokerIsActive.get()) {

                        /*
                         * Await next available resource token in pool.
                         */

                        Object resourceToken = resourceTokenPool.take();

                        boolean resourceTokenInUse = false;

                        try {

                            RBrokerWorker worker =
                                    createBrokerWorker(nextTaskInQueue,
                                            executorTaskCounter.getAndIncrement(),
                                            priorityTaskAvailable,
                                            resourceToken,
                                            RBrokerEngine.this);

                            resourceTokenInUse = true;
                            taskResourceTokenMap.put(nextTaskInQueue, resourceToken);

                            Future future = taskWorkerExecutor.submit(worker);

                            RTaskTokenListener taskTokenListener =
                                    taskTokenListenerMap.remove(nextTaskInQueue);

                            if (taskTokenListener != null) {
                                taskTokenListener.onTask(nextTaskInQueue, future);
                            } else {
                                System.out.println("RBrokerEngine: " +
                                        "taskTokenListener callback not found for " +
                                        nextTaskInQueue + ", unexpected error.");
                            }

                        } catch (Exception ex) {

                            if (!resourceTokenInUse && resourceToken != null) {
                                /*
                                 * Return unused RProject instance
                                 * to the tail of the project pool.
                                 */
                                resourceTokenPool.add(resourceToken);
                            }

                            System.out.println("RBrokerEngine: " +
                                    " processing task " + nextTaskInQueue +
                                    ", ex=" + ex);
                        }

                    } // nextTaskInQueue != null

                } // while taskBrokerIsActive


            } catch (Exception mex) {
                System.out.println("RBrokerEngine: " +
                        "brokerEngineExecutor.run ex=" + mex);
            }
        }

    }

    private class RBrokerListenerManager implements Runnable {

        final public void run() {

            while (taskBrokerIsActive.get()) {

                int tasksHandledOnLoop = 0;

                try {

                    while (liveTaskTokens.isEmpty() &&
                            taskBrokerIsActive.get()) {
                        try {
                            Thread.currentThread().sleep(
                                    LIVE_TASK_TOKEN_PEEK_INTERVAL);
                        } catch (InterruptedException iex) {
                        }
                    }

                    for (RTaskToken rTaskToken : liveTaskTokens) {

                        boolean repeatTaskFound = false;

                        if (rTaskToken.isDone()) {

                            RTaskResult result = null;

                            try {

                                // Extract task result.
                                result =
                                        (RTaskResult) rTaskToken.getResult();

                                if (taskListener != null) {

                                    if (((RTaskResultImpl) result).repeatTask) {
                                        repeatTaskFound = true;
                                    } else {

                                        try {
                                            taskListener.onTaskCompleted(
                                                    rTaskToken.getTask(), result);
                                        } catch (Exception ontcx) {
                                            /*
                                             * RBrokerEngine onTaskCompleted
                                             * is * calling back into client
                                             * application code. That code
                                             * could erroneously throw an
                                             * Exception back into
                                             * RBrokerEngine. If so, swallow
                                             * it.
                                             */
                                        }

                                    }
                                }

                            } catch (Exception ex) {

                                Throwable cause = ex;
                                if (ex instanceof ExecutionException)
                                    cause = ex.getCause();

                                if (taskListener != null) {

                                    try {
                                        taskListener.onTaskError(
                                                rTaskToken.getTask(), cause);
                                    } catch (Exception ontex) {
                                        /*
                                         * RBrokerEngine onTaskError is
                                         * calling back into client application
                                         * code. That code could erroneously
                                         * throw an Exception back into
                                         * RBrokerEngine. If so, swallow it.
                                         */
                                    }
                                }
                            }

                            liveTaskTokens.remove(rTaskToken);

                            if (!repeatTaskFound) {
                                tasksHandledOnLoop++;
                                updateBrokerStats(result);
                            }

                        }

                    }

                } catch (Exception ex) {
                    if (brokerListener != null) {
                        brokerListener.onRuntimeError(ex);
                    }
                }

                if (tasksHandledOnLoop > 0) {
                    if (brokerListener != null) {
                        brokerListener.onRuntimeStats(buildStats(),
                                maxConcurrency());
                    }
                }

            } // while taskBrokerIsActive
        }

        /*
         * Updates RBrokerRuntimeStats for RBroker.
         */
        private void updateBrokerStats(RTaskResult result) {

            totalTasksRunByBroker.incrementAndGet();

            if (result.isSuccess()) {
                totalTasksRunToSuccess.incrementAndGet();
            }
            totalTaskTimeOnCode.addAndGet(result.getTimeOnCode());
            totalTaskTimeOnServer.addAndGet(result.getTimeOnServer());
            totalTaskTimeOnCall.addAndGet(result.getTimeOnCall());
        }

        /*
         * Builds RBrokerRuntimeStats for RBroker
         * to publish onEngineStats().
         */
        private RBrokerRuntimeStats buildStats() {

            RBrokerRuntimeStats stats = new RBrokerRuntimeStats();
            stats.totalTasksRun = totalTasksRunByBroker.get();
            stats.totalTasksRunToSuccess = totalTasksRunToSuccess.get();
            stats.totalTasksRunToFailure = stats.totalTasksRun - stats.totalTasksRunToSuccess;
            stats.totalTimeTasksOnCode = totalTaskTimeOnCode.get();
            stats.totalTimeTasksOnServer = totalTaskTimeOnServer.get();
            stats.totalTimeTasksOnCall = totalTaskTimeOnCall.get();
            return stats;
        }

        private AtomicLong totalTasksRunByBroker = new AtomicLong();
        private AtomicLong totalTasksRunToSuccess = new AtomicLong();
        private AtomicLong totalTaskTimeOnCode = new AtomicLong();
        private AtomicLong totalTaskTimeOnServer = new AtomicLong();
        private AtomicLong totalTaskTimeOnCall = new AtomicLong();
    }
}
