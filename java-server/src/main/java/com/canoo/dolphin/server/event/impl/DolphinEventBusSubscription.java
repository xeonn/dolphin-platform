package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.event.Subscription;

public class DolphinEventBusSubscription implements Subscription {

    private DolphinEventBusImpl dolphinEventBus;
    private final String topic;
    private final MessageListener handler;

    public DolphinEventBusSubscription(DolphinEventBusImpl dolphinEventBus, String topic, MessageListener handler) {
        this.dolphinEventBus = dolphinEventBus;
        this.topic = topic;
        this.handler = handler;
    }

    @Override
    public void unsubscribe() {
        dolphinEventBus.unsubscribe(this);
    }

    public String getTopic() {
        return topic;
    }

    public MessageListener getHandler() {
        return handler;
    }
}
