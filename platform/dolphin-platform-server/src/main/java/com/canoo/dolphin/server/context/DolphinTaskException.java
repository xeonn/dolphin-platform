package com.canoo.dolphin.server.context;

public class DolphinTaskException extends RuntimeException {

    public DolphinTaskException(String message) {
        super(message);
    }

    public DolphinTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinTaskException(Throwable cause) {
        super(cause);
    }
}
