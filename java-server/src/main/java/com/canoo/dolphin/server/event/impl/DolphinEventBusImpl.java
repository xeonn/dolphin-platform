package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.server.EventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DolphinEventBusImpl implements DolphinEventBus {

    private static final DolphinEventBusImpl instance = new DolphinEventBusImpl();

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    private final EventBus eventBus = new EventBus();

    private final DataflowQueue sender = new DataflowQueue();

    private final Object releaseVal = new Object();

    private Map<String, Receiver> receiverPerSession = new ConcurrentHashMap<>();

    private DolphinEventBusImpl() {
    }

    public void publish(String topic, Object data) {
        eventBus.publish(sender, new MessageImpl(topic, data));
    }

    public Subscription subscribe(String topic, MessageListener handler) {
        return getReceiverInSession(true).subscribe(this, topic, handler);
    }

    protected String getDolphinId() {
        return DefaultDolphinServlet.getDolphinId();
    }

    public void unsubscribe(DolphinEventBusSubscription subscription) {
        Receiver receiverInSession = getReceiverInSession(false);
        if (receiverInSession != null) {
            receiverInSession.unsubscribe(subscription.getTopic(), subscription.getHandler());
        }
    }

    public void unsubscribeSession(String dolphinId) {
        Receiver receiver = receiverPerSession.remove(dolphinId);
        if (receiver != null) {
            receiver.unsubscribeFromEventBus(eventBus);
            receiver.unsubscribeAllTopics();
        }
    }

    private Receiver getReceiverInSession(boolean create) {
        String dolphinId = getDolphinId();
        Receiver receiver = receiverPerSession.get(dolphinId);
        if (receiver == null && create) {
            receiver = new Receiver();
            receiverPerSession.put(dolphinId, receiver);
        }
        return receiver;
    }


    /**
     * this method blocks till a release event occurs or there is something to handle in this session
     * this is the only location where we subscribe to our eventBus.
     * So if listenOnEventsForCurrentDolphinSession is not called from client, we will never listen to the eventBus.
     * TODO it is not optimal to try to get events after 20 milliseconds, because if there are many events we will never return
     */
    public void listenOnEventsForCurrentDolphinSession() throws InterruptedException {
        String dolphinId = getDolphinId();
        System.out.println("long poll call from dolphin session " + dolphinId);
        Receiver receiverInSession = getReceiverInSession(true);
        if (!receiverInSession.isListeningToEventBus()) {
            receiverInSession.subscribeToEventBus(eventBus);
        }
        DataflowQueue receiverQueue = receiverInSession.getReceiverQueue();

        boolean somethingHandled = false;

        while (!somethingHandled) {
            //blocking call
            Object val = receiverQueue.getVal();

            while (val != null) {
                if (val == releaseVal) {
                    return;
                }
                Message event = (Message) val;
                System.out.println("handle event for dolphinId: " + dolphinId);
                somethingHandled |= receiverInSession.handle(event);
                val = receiverQueue.getVal(20, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * TODO we must release per session!!!
     */
    @SuppressWarnings("unchecked")
    public void release() {
        for (Receiver receiver : receiverPerSession.values()) {
            if (receiver.isListeningToEventBus()) {
                receiver.getReceiverQueue().leftShift(releaseVal);
            }
        }
    }
}
