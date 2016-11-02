package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.IdentitySet;
import com.canoo.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.dolphin.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Internal class to call tasks (see {@link Runnable}) in a Dolphin Platform context
 * (see {@link DolphinContext}). Tasks can come from an "invokeLater" call or the event bus.
 */
public class DolphinContextTaskQueue {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContextTaskQueue.class);

    private final IdentitySet<Runnable> tasks;

    private final String contextId;

    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final long maxExecutionTime = 10_000;

    private final long sleepTime = 1_000;

    public DolphinContextTaskQueue(final String contextId) {
        this.contextId = Assert.requireNonBlank(contextId, "contextId");
        this.tasks = new IdentitySet<>();
    }

    public void addTask(Runnable task) {
        tasks.add(task);
        LOG.trace("Tasks added to Dolphin Platform context {}", contextId);
    }

    public void interrupt() {
        interrupted.set(true);
        LOG.trace("Tasks in Dolphin Platform context {} interrupted", contextId);
    }

    public synchronized void executeTasks() {
        if (!DolphinPlatformBootstrap.getInstance().isCurrentContext(contextId)) {
            throw new IllegalStateException("Not in Dolphin Platform context " + contextId);
        }
        LOG.trace("Running {} tasks in Dolphin Platform context {}", tasks.size(), contextId);
        long startTime = System.currentTimeMillis();
        while (!interrupted.get() && System.currentTimeMillis() < startTime + maxExecutionTime) {
            Iterator<Runnable> taskIterator = tasks.iterator();
            if (taskIterator.hasNext()) {
                Runnable task = taskIterator.next();
                try {
                    task.run();
                    LOG.trace("Executed task in Dolphin Platform context {}", contextId);
                } catch (Exception e) {
                    throw new DolphinTaskException("Error in running task in Dolphin Platform context " + contextId, e);
                } finally {
                    tasks.remove(task);
                }
            }
            try {
                //TODO: refactoring - do not use sleep
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new DolphinTaskException("Task executor error in Dolphin Platform context " + contextId, e);
            }
        }
        interrupted.set(false);
        LOG.trace("Task executor in Dolphin Platform context {} interrupted after {} ms. Still {} tasks open", contextId, System.currentTimeMillis() - startTime, tasks.size());
    }
}
