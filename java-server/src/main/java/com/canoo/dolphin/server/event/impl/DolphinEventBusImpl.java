package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.server.EventBus;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * TODO locking is wrong and suboptimal.
 * TODO release should only happen per dolphin session.
 * TODO maybe introduce a dataFlowQueue per session and topic to reduce looping.
 */
public class DolphinEventBusImpl implements DolphinEventBus {

    private static DolphinEventBusImpl instance = new DolphinEventBusImpl();

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    private EventBus eventBus = new EventBus();

    private DataflowQueue sender = new DataflowQueue();

    private Lock lock = new ReentrantLock();

    private final Object releaseVal = new Object();

    private Map<String, DataflowQueue> receiverPerSession = new HashMap<>();

    private Map<String, Set<MessageListener>> handlersPerSession = new HashMap<String, Set<MessageListener>>() {
        @Override
        public Set<MessageListener> get(Object key) {
            Set<MessageListener> eventHandlers = super.get(key);
            if (eventHandlers == null) {
                eventHandlers = new HashSet<>();
                put(key.toString(), eventHandlers);
            }
            return eventHandlers;
        }
    };

    private Map<String, List<MessageListener>> handlersPerTopic = new HashMap<String, List<MessageListener>>() {
        @Override
        public List<MessageListener> get(Object key) {
            List<MessageListener> eventHandlers = super.get(key);
            if (eventHandlers == null) {
                eventHandlers = new ArrayList<>();
                put(key.toString(), eventHandlers);
            }
            return eventHandlers;
        }
    };

    private DolphinEventBusImpl() {
    }

    public void publish(String topic, Object data) {
        eventBus.publish(sender, new Message(topic, data));
    }

    public Subscription subscribe(String topic, MessageListener handler) {
        lock.lock();
        try {
            String dolphinId = getDolphinId();
            DataflowQueue currentReceiver = receiverPerSession.get(dolphinId);
            if (currentReceiver == null) {
                currentReceiver = new DataflowQueue();
                receiverPerSession.put(dolphinId, currentReceiver);
                eventBus.subscribe(currentReceiver);
            }
            handlersPerSession.get(dolphinId).add(handler);
            handlersPerTopic.get(topic).add(handler);
            return new SubscriptionImpl(this, topic, handler);
        } finally {
            lock.unlock();
        }
    }

    protected String getDolphinId() {
        return DefaultDolphinServlet.getDolphinId();
    }

    public void unregisterHandler(SubscriptionImpl subscription) {
        lock.lock();
        try {
            MessageListener handler = subscription.getHandler();
            handlersPerTopic.get(subscription.getTopic()).remove(handler);
            String dolphinId = getDolphinId();
            handlersPerSession.get(dolphinId).remove(handler);
            if (handlersPerSession.get(dolphinId).isEmpty()) {
                eventBus.unSubscribe(receiverPerSession.get(dolphinId));
            }
        } finally {
            lock.unlock();
        }
    }

    public void unregisterDolphinSession(String dolphinId) {
        lock.lock();
        try {
            Set<MessageListener> eventHandlers = handlersPerSession.remove(dolphinId);
            for (Set<MessageListener> eventHandlerSet : handlersPerSession.values()) {
                for (MessageListener eventHandler : eventHandlers) {
                    eventHandlerSet.remove(eventHandler);
                }
            }
            DataflowQueue dataflowQueue = receiverPerSession.remove(dolphinId);
            if (dataflowQueue != null) {
                eventBus.unSubscribe(dataflowQueue);
            }
        } finally {
            lock.unlock();
        }
    }

    public void listenOnEventsForCurrentDolphinSession(long time, TimeUnit unit) throws InterruptedException {
        String dolphinId = getDolphinId();
        DataflowQueue receiver = receiverPerSession.get(dolphinId);
        if (receiver == null) {
            receiver = new DataflowQueue();
            receiverPerSession.put(dolphinId, receiver);
        }
        Object val = receiver.getVal(time, unit);
        if (val == releaseVal) {
            return;
        }
        Message event = (Message) val;
        while (event != null) {
            String topic = event.getTopic();
            lock.lock();
            try {
                List<MessageListener> eventHandlers = handlersPerTopic.get(topic);
                for (MessageListener eventHandler : eventHandlers) {
                    if (handlersPerSession.get(dolphinId).contains(eventHandler)) {
                        eventHandler.onMessage(event);
                    }
                }
            } finally {
                lock.unlock();
            }
            event = (Message) receiver.getVal(20, TimeUnit.MILLISECONDS);
        }
    }

    @SuppressWarnings("unchecked")
    public void release() {
        lock.lock();
        try {
            for (DataflowQueue dataflowQueue : receiverPerSession.values()) {
                if (dataflowQueue != null) {
                    dataflowQueue.leftShift(releaseVal);
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
