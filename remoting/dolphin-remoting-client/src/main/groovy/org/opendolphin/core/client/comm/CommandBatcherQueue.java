package org.opendolphin.core.client.comm;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandBatcherQueue implements DataflowQueue<List<CommandAndHandler>> {

    private final List<List<CommandAndHandler>> internalQueue = new LinkedList<>();

    private final Lock queueLock = new ReentrantLock();

    private final Condition emptyCondition = queueLock.newCondition();

    @Override
    public List<CommandAndHandler> getVal() throws InterruptedException {
        queueLock.lock();
        try {
            if (internalQueue.isEmpty()) {
                emptyCondition.await();
            }
            if (internalQueue.isEmpty()) {
                return null;
            }
            return internalQueue.remove(0);
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public List<CommandAndHandler> getVal(long value, TimeUnit timeUnit) throws InterruptedException {
        queueLock.lock();
        try {
            if (internalQueue.isEmpty()) {
                emptyCondition.await(value, timeUnit);
            }
            if (internalQueue.isEmpty()) {
                return null;
            }
            return internalQueue.remove(0);
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void add(List<CommandAndHandler> value) {
        queueLock.lock();
        try {
            internalQueue.add(value);
            emptyCondition.signal();
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public int length() {
        queueLock.lock();
        try {
            return internalQueue.size();
        } finally {
            queueLock.unlock();
        }
    }
};
