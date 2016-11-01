package com.canoo.dolphin.converter;

public class ValueConverterException extends Exception {

    public ValueConverterException(String message) {
        super(message);
    }

    public ValueConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueConverterException(Throwable cause) {
        super(cause);
    }
}
