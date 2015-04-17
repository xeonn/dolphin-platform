package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Event;
import com.canoo.dolphin.server.event.EventHandler;
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

    private Map<String, Set<EventHandler>> handlersPerSession = new HashMap<String, Set<EventHandler>>() {
        @Override
        public Set<EventHandler> get(Object key) {
            Set<EventHandler> eventHandlers = super.get(key);
            if (eventHandlers == null) {
                eventHandlers = new HashSet<>();
                put(key.toString(), eventHandlers);
            }
            return eventHandlers;
        }
    };


    private Map<String, List<EventHandler>> handlers = new HashMap<String, List<EventHandler>>() {
        @Override
        public List<EventHandler> get(Object key) {
            List<EventHandler> eventHandlers = super.get(key);
            if (eventHandlers == null) {
                eventHandlers = new ArrayList<>();
                put(key.toString(), eventHandlers);
            }
            return eventHandlers;
        }
    };

    private DolphinEventBusImpl() {
    }

    public void publish(String address, Object value) {
        eventBus.publish(sender, new Event(address, value));
    }

    public void registerHandler(String address, EventHandler handler) {
        lock.lock();
        try {
            String dolphinId = getDolphinId();
            handlersPerSession.get(dolphinId).add(handler);
            handlers.get(address).add(handler);
        } finally {
            lock.unlock();
        }
    }

    protected String getDolphinId() {
        return DefaultDolphinServlet.getDolphinId();
    }

    public void unregisterHandler(String address, EventHandler handler) {
        lock.lock();
        try {
            handlers.get(address).remove(handler);
            handlersPerSession.get(getDolphinId()).remove(handler);
        } finally {
            lock.unlock();
        }
    }

    public void unregisterHandlersForCurrentDolphinSession() {
        lock.lock();
        try {
            Set<EventHandler> eventHandlers = handlersPerSession.remove(getDolphinId());
            for (Set<EventHandler> eventHandlerSet : handlersPerSession.values()) {
                for (EventHandler eventHandler : eventHandlers) {
                    eventHandlerSet.remove(eventHandler);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void listenOnEventsForCurrentDolphinSession(long time, TimeUnit unit) throws InterruptedException {
        Event event = (Event) sender.getVal(time, unit);
        while (event != null) {
            String address = event.getAddress();
            lock.lock();
            try {
                List<EventHandler> eventHandlers = handlers.get(address);
                for (EventHandler eventHandler : eventHandlers) {
                    String dolphinId = getDolphinId();
                    if (handlersPerSession.get(dolphinId).contains(eventHandler)) {
                        eventHandler.onEvent(event);
                    }
                }
            } finally {
                lock.unlock();
            }
            event = (Event) sender.getVal(20, TimeUnit.MILLISECONDS);
        }

    }
}
