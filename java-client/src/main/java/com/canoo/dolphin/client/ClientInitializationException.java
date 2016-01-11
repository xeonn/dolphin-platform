package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 11.01.16.
 */
public class ClientInitializationException extends RuntimeException {

    public ClientInitializationException(String message) {
        super(message);
    }

    public ClientInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientInitializationException(Throwable cause) {
        super(cause);
    }

}
