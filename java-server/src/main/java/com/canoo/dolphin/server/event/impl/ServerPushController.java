package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

import java.util.concurrent.TimeUnit;

@DolphinController("ServerPushController")
public class ServerPushController {

    @DolphinAction
    public void longPoll() {
        try {
            DolphinEventBusImpl.getInstance().listenOnEventsForCurrentDolphinSession(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            //this exception is called when the clients has an update and needs to stop the polling.
            // Therefore we do nothing here
        }
    }

    @DolphinAction
    public void release() {
        DolphinEventBusImpl.getInstance().release();
    }
}
