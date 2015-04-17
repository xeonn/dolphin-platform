package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 17.04.15.
 */
public class Event {

    private final String address;

    private final Object message;

    private final long sendTimestamp;

    public Event(String address, Object message) {
        this.address = address;
        this.message = message;
        this.sendTimestamp = System.currentTimeMillis();
    }

    public String getAddress() {
        return address;
    }

    public Object getMessage() {
        return message;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }
}
