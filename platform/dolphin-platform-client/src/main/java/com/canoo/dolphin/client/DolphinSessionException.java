package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class DolphinSessionException extends RuntimeException {

    public DolphinSessionException() {
    }

    public DolphinSessionException(String message) {
        super(message);
    }

    public DolphinSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinSessionException(Throwable cause) {
        super(cause);
    }
}
