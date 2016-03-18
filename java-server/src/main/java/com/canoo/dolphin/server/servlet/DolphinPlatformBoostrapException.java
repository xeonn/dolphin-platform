package com.canoo.dolphin.server.servlet;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class DolphinPlatformBoostrapException extends RuntimeException {

    public DolphinPlatformBoostrapException() {
    }

    public DolphinPlatformBoostrapException(String message) {
        super(message);
    }

    public DolphinPlatformBoostrapException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinPlatformBoostrapException(Throwable cause) {
        super(cause);
    }
}
