package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinEventBusImpl implements DolphinEventBus {

    private static DolphinEventBusImpl instance = new DolphinEventBusImpl();

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    public void publish(String address, Object value) {

    }

    public HandlerIdentifier registerHandler(String address, EventHandler handler) {
        return null;
    }

    public void unregisterHandler(HandlerIdentifier handlerIdentifier) {

    }

    public void unregisterHandlersForCurrentDolphinSession() {

    }

    public void sendEventsForCurrentDolphinSession() {

    }
}
