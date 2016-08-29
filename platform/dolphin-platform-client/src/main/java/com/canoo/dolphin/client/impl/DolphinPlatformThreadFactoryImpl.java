package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.DolphinPlatformThreadFactory;
import com.canoo.dolphin.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DolphinPlatformThreadFactoryImpl implements DolphinPlatformThreadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformThreadFactoryImpl.class);

    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final Lock uncaughtExceptionHandlerLock = new ReentrantLock();

    public DolphinPlatformThreadFactoryImpl() {
        this.uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exception) {
                Assert.requireNonNull(thread, "thread");
                Assert.requireNonNull(exception, "exception");
                LOG.error("Unhandled error in Dolphin Platform background thread " + thread.getName(), exception);
            }
        };
    }

    @Override
    public Thread newThread(final Runnable task) {
        Assert.requireNonNull(task, "task");
        return AccessController.doPrivileged((PrivilegedAction<Thread>) () -> {
            final Thread backgroundThread = new Thread(task);
            backgroundThread.setName("Dolphin-Platform-Background-Thread" + threadNumber.getAndIncrement());
            backgroundThread.setDaemon(false);
            uncaughtExceptionHandlerLock.lock();
            try {
                backgroundThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            } finally {
                uncaughtExceptionHandlerLock.unlock();
            }
            return backgroundThread;
        });
    }

    @Override
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        Assert.requireNonNull(uncaughtExceptionHandler, "uncaughtExceptionHandler");
        uncaughtExceptionHandlerLock.lock();
        try {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        } finally {
            uncaughtExceptionHandlerLock.unlock();
        }
    }
}
