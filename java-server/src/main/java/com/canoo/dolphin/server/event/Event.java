package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 17.04.15.
 */
public class Event<T> {

    private String address;

    private T message;

    private long sendTimestamp;

    protected Event(String address, T message, long sendTimestamp) {
        this.address = address;
        this.message = message;
        this.sendTimestamp = sendTimestamp;
    }

    public String getAddress() {
        return address;
    }

    public T getMessage() {
        return message;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }
}
