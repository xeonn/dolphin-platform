package com.canoo.dolphin.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hendrikebbers on 29.08.16.
 */
public class DolphinRumtimeException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinRumtimeException.class);

    private final Thread thread;

    public DolphinRumtimeException(String message, Throwable cause) {
        this(Thread.currentThread(), message, cause);
    }

    public DolphinRumtimeException(Thread thread, String message, Throwable cause) {
        super(message, cause);
        if(thread != null) {
            this.thread = thread;
        } else {
            LOG.error("Can not specify thread for Dolphin Platform runtime error!");
            this.thread = Thread.currentThread();
        }
    }

    public Thread getThread() {
        return thread;
    }
}
