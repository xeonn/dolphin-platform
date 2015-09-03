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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * represents one listener to the queue. It need not to be thread safe, because it is related to one dolphin session.
 */
public class Receiver {

    DataflowQueue receiverQueue;

    Map<String, List<MessageListener>> listenersPerTopic = new HashMap<>();

    public DataflowQueue getReceiverQueue() {
        return receiverQueue;
    }

    public Subscription subscribe(final String topic, final MessageListener handler) {
        if(topic == null || topic.length() == 0) {
            throw new IllegalArgumentException("topic mustn't be empty!");
        }
        if(handler == null) {
            throw new IllegalArgumentException("handler mustn't be empty!");
        }
        List<MessageListener> messageListeners = listenersPerTopic.get(topic);
        if (messageListeners == null) {
            messageListeners = new CopyOnWriteArrayList<>();
            listenersPerTopic.put(topic, messageListeners);
        }
        messageListeners.add(handler);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                List<MessageListener> messageListeners = listenersPerTopic.get(topic);
                if (messageListeners == null) {
                    return;
                }
                messageListeners.remove(handler);
                if (messageListeners.isEmpty()) {
                    listenersPerTopic.remove(topic);
                }
            }
        };
    }

    public boolean handle(Message event) {
        List<MessageListener> messageListeners = listenersPerTopic.get(event.getTopic());
        if (messageListeners == null || messageListeners.isEmpty()) {
            return false;
        }
        // iterate over copy, because the list could be changed in an onMessage method
        for (MessageListener messageListener : messageListeners) {
            messageListener.onMessage(event);
        }
        return true;
    }

    public boolean isListeningToEventBus() {
        return receiverQueue != null;
    }

    public void unregister(EventBus eventBus) {
        if(eventBus == null) {
            throw new IllegalArgumentException("eventBus mustn't be empty!");
        }
        if (isListeningToEventBus()) {
            eventBus.unSubscribe(receiverQueue);
            receiverQueue = null;
        }
        listenersPerTopic.clear();
    }

    public void register(EventBus eventBus) {
        if(eventBus == null) {
            throw new IllegalArgumentException("eventBus mustn't be empty!");
        }
        if (isListeningToEventBus()) {
            return;
        }
        receiverQueue = new DataflowQueue();
        eventBus.subscribe(receiverQueue);
    }
}
