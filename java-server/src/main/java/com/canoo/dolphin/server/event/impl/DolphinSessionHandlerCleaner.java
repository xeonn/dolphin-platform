package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DolphinContext dolphinContext = DolphinContextHandler.getContext(se.getSession());
        if (dolphinContext != null) {
            DolphinEventBusImpl.getInstance().unsubscribeSession(dolphinContext.getId());
        }
    }
}
