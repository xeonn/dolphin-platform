package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 03.10.15.
 */
public class ControllerActionException extends Exception {

    public ControllerActionException() {
    }

    public ControllerActionException(String message) {
        super(message);
    }

    public ControllerActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerActionException(Throwable cause) {
        super(cause);
    }
}
