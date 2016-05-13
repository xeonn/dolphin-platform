package com.canoo.dolphin.mapping;

/**
 * Exception that is thrown if an error happens in the Dolphin Platform model mapping. This can happen if a
 * model class definition isn't working for the Dolphin Platform.
 */
public class MappingException extends RuntimeException {

    /**
     * Default constructor
     */
    public MappingException() {
    }

    /**
     * Constructor
     * @param message error message
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message error message
     * @param cause the cause
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause the cause
     */
    public MappingException(Throwable cause) {
        super(cause);
    }
}
