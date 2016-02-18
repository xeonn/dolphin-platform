package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 18.02.16.
 */
public class ControllerInitalizationException extends RuntimeException {

    public ControllerInitalizationException() {
    }

    public ControllerInitalizationException(String message) {
        super(message);
    }

    public ControllerInitalizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerInitalizationException(Throwable cause) {
        super(cause);
    }
}
