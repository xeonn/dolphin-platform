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


    private Map<String, List<MessageHandler>> handlers = new HashMap<String, List<MessageHandler>>() {
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
            handlersPerSession.get(dolphinId).add(handler);
            handlers.get(topic).add(handler);
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
            handlers.get(topic).remove(handler);
            handlersPerSession.get(getDolphinId()).remove(handler);
        } finally {
            lock.unlock();
        }
    }

    public void unregisterHandlersForCurrentDolphinSession() {
        lock.lock();
        try {
            Set<MessageHandler> eventHandlers = handlersPerSession.remove(getDolphinId());
            for (Set<MessageHandler> eventHandlerSet : handlersPerSession.values()) {
                for (MessageHandler eventHandler : eventHandlers) {
                    eventHandlerSet.remove(eventHandler);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void listenOnEventsForCurrentDolphinSession(long time, TimeUnit unit) throws InterruptedException {
        Message event = (Message) sender.getVal(time, unit);
        while (event != null) {
            String topic = event.getTopic();
            lock.lock();
            try {
                List<MessageHandler> eventHandlers = handlers.get(topic);
                for (MessageHandler eventHandler : eventHandlers) {
                    String dolphinId = getDolphinId();
                    if (handlersPerSession.get(dolphinId).contains(eventHandler)) {
                        eventHandler.onMessage(event);
                    }
                }
            } finally {
                lock.unlock();
            }
            event = (Message) sender.getVal(20, TimeUnit.MILLISECONDS);
        }

    }
}
