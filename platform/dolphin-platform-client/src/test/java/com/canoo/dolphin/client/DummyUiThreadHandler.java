package com.canoo.dolphin.client;

import org.opendolphin.core.client.comm.UiThreadHandler;

/**
 * Created by hendrikebbers on 14.07.16.
 */
public class DummyUiThreadHandler implements UiThreadHandler {
    @Override
    public void executeInsideUiThread(Runnable runnable) {
        runnable.run();
    }
}
