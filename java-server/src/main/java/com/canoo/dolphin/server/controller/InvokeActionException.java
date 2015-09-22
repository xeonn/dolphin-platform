package com.canoo.dolphin.server.controller;

/**
 * Created by hendrikebbers on 22.09.15.
 */
public class InvokeActionException extends Exception {

    public InvokeActionException() {
    }

    public InvokeActionException(String message) {
        super(message);
    }

    public InvokeActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeActionException(Throwable cause) {
        super(cause);
    }
}
