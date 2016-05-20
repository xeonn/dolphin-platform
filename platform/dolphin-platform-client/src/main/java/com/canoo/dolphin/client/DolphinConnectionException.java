package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class DolphinConnectionException extends Exception {

    public DolphinConnectionException() {
    }

    public DolphinConnectionException(String message) {
        super(message);
    }

    public DolphinConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinConnectionException(Throwable cause) {
        super(cause);
    }
}
