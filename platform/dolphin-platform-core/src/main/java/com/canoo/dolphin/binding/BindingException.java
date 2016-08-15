package com.canoo.dolphin.binding;

public class BindingException extends RuntimeException {

    private static final long serialVersionUID = 2923090454418134058L;

    public BindingException() {
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
