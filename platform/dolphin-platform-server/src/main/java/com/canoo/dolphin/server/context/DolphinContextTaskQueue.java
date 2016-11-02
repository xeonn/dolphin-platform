package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.dolphin.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Internal class to call tasks (see {@link Runnable}) in a Dolphin Platform context
 * (see {@link DolphinContext}). Tasks can come from an "invokeLater" call or the event bus.
 */
public class DolphinContextTaskQueue {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContextTaskQueue.class);

    private final IdentitySet<Runnable> tasks;

    private final String contextId;

    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final long maxExecutionTime;

    private final TimeUnit maxExecutionTimeUnit;

    private final Lock taskLock = new ReentrantLock();

    private final Condition taskCondition = taskLock.newCondition();

    public DolphinContextTaskQueue(final String contextId, long maxExecutionTime, TimeUnit maxExecutionTimeUnit) {
        this.contextId = Assert.requireNonBlank(contextId, "contextId");
        this.tasks = new IdentitySet<>();
        this.maxExecutionTime = maxExecutionTime;
        this.maxExecutionTimeUnit = maxExecutionTimeUnit;
    }

    public void addTask(Runnable task) {
        taskLock.lock();
        try {
            tasks.add(task);
            LOG.trace("Tasks added to Dolphin Platform context {}", contextId);
            taskCondition.signal();
        } finally {
            taskLock.unlock();
        }
    }

    public void interrupt() {
        taskLock.lock();
        try {
            interrupted.set(true);
            LOG.trace("Tasks in Dolphin Platform context {} interrupted", contextId);
            taskCondition.signal();
        } finally {
            taskLock.unlock();
        }
    }

    public synchronized void executeTasks() {
        if (!DolphinPlatformBootstrap.getInstance().isCurrentContext(contextId)) {
            throw new IllegalStateException("Not in Dolphin Platform context " + contextId);
        }
        taskLock.lock();
        try {
            LOG.trace("Running {} tasks in Dolphin Platform context {}", tasks.size(), contextId);
            long endTime = System.currentTimeMillis() + maxExecutionTimeUnit.toMillis(maxExecutionTime);
            while (!interrupted.get()) {
                if (tasks.isEmpty()) {
                    try {
                        if (!taskCondition.await(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)) {
                            interrupted.set(true);
                        } else {
                            if (interrupted.get()) {
                                interrupted.set(false);
                                return;
                            }
                        }
                    } catch (InterruptedException e) {
                        interrupted.set(true);
                    }
                } else {
                    Iterator<Runnable> taskIterator = tasks.iterator();
                    if (taskIterator.hasNext()) {
                        Runnable task = taskIterator.next();
                        try {
                            task.run();
                            LOG.trace("Executed task in Dolphin Platform context {}", contextId);
                        } catch (Exception e) {
                            throw new DolphinTaskException("Error in running task in Dolphin Platform context " + contextId, e);
                        } finally {
                            interrupted.set(true);
                            tasks.remove(task);
                        }
                    } else {
                        interrupted.set(true);
                    }
                }
            }
            interrupted.set(false);
            LOG.trace("Task executor in Dolphin Platform context {} interrupted. Still {} tasks open", contextId, tasks.size());
        } finally {
            taskLock.unlock();
        }
    }
}
