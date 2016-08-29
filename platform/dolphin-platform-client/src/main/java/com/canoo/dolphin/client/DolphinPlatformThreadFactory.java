package com.canoo.dolphin.client;

import java.util.concurrent.ThreadFactory;

public interface DolphinPlatformThreadFactory extends ThreadFactory {

    void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler);
}
