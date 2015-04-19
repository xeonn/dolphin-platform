package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.server.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receiver {

    DataflowQueue receiverQueue;

    Map<String, List<MessageListener>> listenersPerTopic = new HashMap<>();

    public DataflowQueue getReceiverQueue() {
        return receiverQueue;
    }

    public Subscription subscribe(DolphinEventBusImpl eventBus, String topic, MessageListener handler) {
        List<MessageListener> messageListeners = listenersPerTopic.get(topic);
        if (messageListeners == null) {
            messageListeners = new ArrayList<>();
            listenersPerTopic.put(topic, messageListeners);
        }
        messageListeners.add(handler);
        return new DolphinEventBusSubscription(eventBus, topic, handler);
    }

    public void unsubscribe(String topic, MessageListener handler) {
        List<MessageListener> messageListeners = listenersPerTopic.get(topic);
        if (messageListeners == null) {
            return;
        }
        messageListeners.remove(handler);
        if (messageListeners.isEmpty()) {
            listenersPerTopic.remove(topic);
        }
    }

    public void unsubscribeAllTopics() {
        listenersPerTopic.clear();
    }

    public boolean handle(Message event) {
        List<MessageListener> messageListeners = listenersPerTopic.get(event.getTopic());
        if (messageListeners == null || messageListeners.isEmpty()) {
            return false;
        }
        for (MessageListener messageListener : messageListeners) {
            messageListener.onMessage(event);
        }
        return true;
    }

    public boolean isListeningToEventBus() {
        return receiverQueue != null;
    }

    public void unsubscribeFromEventBus(EventBus eventBus) {
        if (isListeningToEventBus()) {
            eventBus.unSubscribe(receiverQueue);
            receiverQueue = null;
        }
    }

    public void subscribeToEventBus(EventBus eventBus) {
        if (isListeningToEventBus()) {
            return;
        }
        receiverQueue = new DataflowQueue();
        eventBus.subscribe(receiverQueue);
    }
}
