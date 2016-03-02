package com.canoo.dolphin.client.javafx;

/**
 * Created by hendrikebbers on 02.03.16.
 */
public class FxmlLoadException extends RuntimeException {

    public FxmlLoadException() {
    }

    public FxmlLoadException(String message) {
        super(message);
    }

    public FxmlLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FxmlLoadException(Throwable cause) {
        super(cause);
    }
}
