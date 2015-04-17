package com.canoo.dolphin.server.event.impl;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DolphinEventBusImpl.getInstance().unregisterHandlersForCurrentDolphinSession(se.getSession());
    }
}
