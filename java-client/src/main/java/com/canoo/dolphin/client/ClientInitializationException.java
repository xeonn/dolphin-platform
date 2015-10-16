package com.canoo.dolphin.client;

/**
 * This exception will be thrown if a {@link ClientContext} can't be created by using {@link ClientContextFactory#connect(ClientConfiguration)}.
 */
public class ClientInitializationException extends RuntimeException {

    /**
     * Default constructor
     * @param cause the cause
     */
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
