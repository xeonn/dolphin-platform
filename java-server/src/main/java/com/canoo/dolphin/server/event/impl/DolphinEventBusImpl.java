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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DolphinEventBusImpl implements DolphinEventBus {

    private static final DolphinEventBusImpl instance = new DolphinEventBusImpl();
    private static final int MAX_POLL_DURATION = 100;

    public static DolphinEventBusImpl getInstance() {
        return instance;
    }

    private final EventBus eventBus = new EventBus();

    private final DataflowQueue sender = new DataflowQueue();

    private final Object releaseVal = new Object();

    //access is only concurrent for different keys. This sync strategy should be sufficient
    private Map<String, Receiver> receiverPerSession = new ConcurrentHashMap<>();

    protected DolphinEventBusImpl() {
    }

    public void publish(final String topic, final Object data) {
        if(topic == null || topic.length() == 0) {
            throw new IllegalArgumentException("topic mustn't be empty!");
        }
        final long timestamp = System.currentTimeMillis();
        eventBus.publish(sender, new MessageImpl(topic, data));
    }

    public Subscription subscribe(final String topic, final MessageListener handler) {
        if(topic == null || topic.length() == 0) {
            throw new IllegalArgumentException("topic mustn't be empty!");
        }
        if(handler == null) {
            throw new IllegalArgumentException("handler mustn't be empty!");
        }
        String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("subscribe was called outside a dolphin session");
        }
        return getOrCreateReceiverInSession(dolphinId).subscribe(topic, handler);
    }

    protected String getDolphinId() {
        return DefaultDolphinServlet.getDolphinId();
    }

    public void unsubscribeSession(final String dolphinId) {
        if(dolphinId == null || dolphinId.length() == 0) {
            throw new IllegalArgumentException("dolphinId mustn't be empty!");
        }
        Receiver receiver = receiverPerSession.remove(dolphinId);
        if (receiver != null) {
            receiver.unregister(eventBus);
        }
    }

    private Receiver getOrCreateReceiverInSession(String dolphinId) {
        if(dolphinId == null || dolphinId.length() == 0) {
            throw new IllegalArgumentException("dolphinId mustn't be empty!");
        }
        Receiver receiver = receiverPerSession.get(dolphinId);
        if (receiver == null) {
            receiver = new Receiver();
            receiverPerSession.put(dolphinId, receiver);
        }
        return receiver;
    }


    /**
     * this method blocks till a release event occurs or there is something to handle in this session.
     * This is the only location where we subscribe to our internal eventBus.
     * So if longPoll is not called from client, we will never listen to the eventBus.
     */
    public void longPoll() throws InterruptedException {
        final String dolphinId = getDolphinId();
        if (dolphinId == null) {
            throw new IllegalStateException("longPoll was called outside a dolphin session");
        }
        //TODO replace by log
        System.out.println("long poll call from dolphin session " + dolphinId);
        final Receiver receiverInSession = getOrCreateReceiverInSession(dolphinId);
        if (!receiverInSession.isListeningToEventBus()) {
            receiverInSession.register(eventBus);
        }
        final DataflowQueue receiverQueue = receiverInSession.getReceiverQueue();

        boolean somethingHandled = false;

        while (!somethingHandled) {
            //blocking call
            Object val = receiverQueue.getVal();

            final long startTime = System.currentTimeMillis();

            while (val != null) {
                if (val == releaseVal) {
                    return;
                }
                final Message event = (Message) val;
                //TODO replace by log
                System.out.println("handle event for dolphinId: " + dolphinId);
                somethingHandled |= receiverInSession.handle(event);

                //if there are many events we would loop forever -> additional exit condition
                if (System.currentTimeMillis() - startTime <= MAX_POLL_DURATION) {
                    val = receiverQueue.getVal(20, MILLISECONDS);
                } else {
                    val = null;
                }
            }
        }
    }

    /**
     * TODO we must release per session!!!
     */
    @SuppressWarnings("unchecked")
    public void release() {
//        //TODO the release should happen in the context of a dolphin session.
//        //TODO this piece of code should be used then
//        String dolphinId = getDolphinId();
//        if (dolphinId == null) {
//            //TODO replace by log
//            System.out.println("warning: release was called outside dolphin session");
//            //TODO warn or throw exception?
//            return;
//        }
//        Receiver receiver = receiverPerSession.get(dolphinId);
//        if (receiver != null && receiver.isListeningToEventBus()) {
//            receiver.getReceiverQueue().leftShift(releaseVal);
//        }


        //TODO remove
        for (Receiver receiver : receiverPerSession.values()) {
            if (receiver.isListeningToEventBus()) {
                receiver.getReceiverQueue().leftShift(releaseVal);
            }
        }
    }

    private final static class MessageImpl implements Message {

        private String topic;

        private Object data;

        private long timestamp;

        private MessageImpl(String topic, Object data) {
            this.topic = topic;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String getTopic() {
            return topic;
        }

        @Override
        public Object getData() {
            return data;
        }

        @Override
        public long getSendTimestamp() {
            return timestamp;
        }
    }
}
