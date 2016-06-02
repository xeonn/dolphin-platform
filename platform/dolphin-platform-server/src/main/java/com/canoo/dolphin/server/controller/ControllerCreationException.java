package com.canoo.dolphin.server.controller;

/**
 * Created by hendrikebbers on 02.06.16.
 */
public class ControllerCreationException extends RuntimeException {

    private static final long serialVersionUID = 2380863641251071460L;

    public ControllerCreationException() {
    }

    public ControllerCreationException(String message) {
        super(message);
    }

    public ControllerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerCreationException(Throwable cause) {
        super(cause);
    }
}
