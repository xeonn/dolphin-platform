package com.canoo.dolphin.server.event;

/**
 * An message of the dolphin platform event bus.
 */
public interface Message {


    /**
     * Returns the topic of the event
     * @return the topic
     */
    String getTopic();

    /**
     * Returns the data of the message
     * @return the data
     */
    Object getData();

    /**
     * Returns the timestamp of the send date of this message
     * @return the timestamp
     */
    long getSendTimestamp();
}
