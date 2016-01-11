package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 11.01.16.
 */
public class ClientShutdownException extends Exception {

    public ClientShutdownException(String message) {
        super(message);
    }

    public ClientShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientShutdownException(Throwable cause) {
        super(cause);
    }
}
