package com.canoo.dolphin.server.event.impl;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Hello");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DolphinEventBusImpl.getInstance().unregisterHandlersForCurrentDolphinSession(se.getSession());
    }
}
