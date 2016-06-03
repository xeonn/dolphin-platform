package com.canoo.dolphin.client;

public class DolphinSessionException extends RuntimeException {

    private static final long serialVersionUID = -661120767433339452L;

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
