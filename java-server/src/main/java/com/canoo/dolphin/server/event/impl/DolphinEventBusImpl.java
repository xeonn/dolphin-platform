package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageHandler;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.server.EventBus;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinEventBusImpl implements DolphinEventBus {

    private static DolphinEventBusImpl instance = new DolphinEventBusImpl();

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    private EventBus eventBus = new EventBus();

    private DataflowQueue sender = new DataflowQueue();

    private Lock lock = new ReentrantLock();

    private Map<String, DataflowQueue> receiverPerSession = new HashMap<>();

    private Map<String, Set<MessageHandler>> handlersPerSession = new HashMap<String, Set<MessageHandler>>() {
        @Override
        public Set<MessageHandler> get(Object key) {
            Set<MessageHandler> eventHandlers = super.get(key);
            if (eventHandlers == null) {
                eventHandlers = new HashSet<>();
                put(key.toString(), eventHandlers);
            }
            return eventHandlers;
        }
    };

    private Map<String, List<MessageHandler>> handlersPerTopic = new HashMap<String, List<MessageHandler>>() {
        @Override
        public List<MessageHandler> get(Object key) {
            List<MessageHandler> eventHandlers = super.get(key);
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

    public void registerHandler(String topic, MessageHandler handler) {
        lock.lock();
        try {
            String dolphinId = getDolphinId();
            DataflowQueue currentReceiver = receiverPerSession.get(dolphinId);
            if (currentReceiver == null) {
                currentReceiver = new DataflowQueue();
                eventBus.subscribe(currentReceiver);
            }
            handlersPerSession.get(dolphinId).add(handler);
            handlersPerTopic.get(topic).add(handler);
        } finally {
            lock.unlock();
        }
    }

    protected String getDolphinId() {
        return DefaultDolphinServlet.getDolphinId();
    }

    public void unregisterHandler(String topic, MessageHandler handler) {
        lock.lock();
        try {
            handlersPerTopic.get(topic).remove(handler);
            String dolphinId = getDolphinId();
            handlersPerSession.get(dolphinId).remove(handler);
            if (handlersPerSession.get(dolphinId).isEmpty()) {
                eventBus.unSubscribe(receiverPerSession.remove(dolphinId));
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterHandler(MessageHandler messageHandler) {
        lock.lock();
        try {
            for (List<MessageHandler> messageHandlers : handlersPerTopic.values()) {
                messageHandlers.remove(messageHandler);
            }
            for (Set<MessageHandler> messageHandlers : handlersPerSession.values()) {
                messageHandlers.remove(messageHandler);
            }
            String dolphinId = getDolphinId();
            if (handlersPerSession.get(dolphinId).isEmpty()) {
                eventBus.unSubscribe(receiverPerSession.remove(dolphinId));
            }
        } finally {
            lock.unlock();
        }
    }


    public void unregisterHandlersForCurrentDolphinSession() {
        lock.lock();
        try {
            String dolphinId = getDolphinId();
            Set<MessageHandler> eventHandlers = handlersPerSession.remove(dolphinId);
            for (Set<MessageHandler> eventHandlerSet : handlersPerSession.values()) {
                for (MessageHandler eventHandler : eventHandlers) {
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
        Message event = (Message) receiver.getVal(time, unit);
        while (event != null) {
            String topic = event.getTopic();
            lock.lock();
            try {
                List<MessageHandler> eventHandlers = handlersPerTopic.get(topic);
                for (MessageHandler eventHandler : eventHandlers) {
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
}
