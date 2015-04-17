package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.mapping.ReferenceIdentifier;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Event;
import com.canoo.dolphin.server.event.EventHandler;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
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

    private DolphinEventBusImpl() {
    }

    public void publish(String address, Object value) {

    }

    public ReferenceIdentifier registerHandler(String address, EventHandler handler) {
        return null;
    }

    public void unregisterHandler(ReferenceIdentifier handlerIdentifier) {

    }

    public void unregisterHandlersForCurrentDolphinSession() {

    }

    public void publishToDolphinSession(String dolphinSession, Event event) {

    }

    public void listenOnEventsForCurrentDolphinSession(long time, TimeUnit unit) throws InterruptedException{

    }
}
