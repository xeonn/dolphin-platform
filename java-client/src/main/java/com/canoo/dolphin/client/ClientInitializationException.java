package com.canoo.dolphin.client;

public class ClientInitializationException extends RuntimeException {

    public ClientInitializationException(Throwable cause) {
        super(cause);
    }

    public ClientInitializationException() {
    }

    public ClientInitializationException(String message) {
        super(message);
    }

    public ClientInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
