package com.canoo.dolphin.client.clientscope;

/**
 * Created by hendrikebbers on 22.09.15.
 */
public class DolphinRemotingException extends RuntimeException {

    public DolphinRemotingException() {
    }

    public DolphinRemotingException(String message) {
        super(message);
    }

    public DolphinRemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinRemotingException(Throwable cause) {
        super(cause);
    }
}
