package com.canoo.dolphin.test;

public class ControllerTestException extends RuntimeException {

    public ControllerTestException() {
    }

    public ControllerTestException(String message) {
        super(message);
    }

    public ControllerTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerTestException(Throwable cause) {
        super(cause);
    }
}
