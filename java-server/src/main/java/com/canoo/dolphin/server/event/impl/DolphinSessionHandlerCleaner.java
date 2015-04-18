package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String dolphinId = DefaultDolphinServlet.getDolphinId(se.getSession());
        DolphinEventBusImpl.getInstance().unregisterDolphinSession(dolphinId);
    }
}
