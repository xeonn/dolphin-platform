package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public interface DolphinEventBus {

    void publish(String address, Object value);

    HandlerIdentifier registerHandler(String address, EventHandler handler);

    void unregisterHandler(HandlerIdentifier handlerIdentifier);
}
