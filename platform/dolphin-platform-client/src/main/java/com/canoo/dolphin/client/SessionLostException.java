package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class SessionLostException extends Exception {

    public SessionLostException() {
    }

    public SessionLostException(String message) {
        super(message);
    }

    public SessionLostException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionLostException(Throwable cause) {
        super(cause);
    }
}
