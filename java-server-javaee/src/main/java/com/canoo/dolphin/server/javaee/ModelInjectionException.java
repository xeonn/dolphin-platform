package com.canoo.dolphin.server.javaee;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class ModelInjectionException extends RuntimeException {

    public ModelInjectionException() {
    }

    public ModelInjectionException(String message) {
        super(message);
    }

    public ModelInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelInjectionException(Throwable cause) {
        super(cause);
    }
}
