package com.canoo.dolphin.server.event;

/**
 * An message of the dolphin platform event bus.
 */
public class Message {

    private final String topic;

    private final Object data;

    private final long sendTimestamp;

    /**
     * Creates a new message. This will normally only used by the dolphin platform
     * @param topic the topic
     * @param data the data
     */
    public Message(String topic, Object data) {
        this.topic = topic;
        this.data = data;
        this.sendTimestamp = System.currentTimeMillis();
    }

    /**
     * Returns the topic of the event
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns the data of the message
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns the timestamp of the send date of this message
     * @return the timestamp
     */
    public long getSendTimestamp() {
        return sendTimestamp;
    }
}
