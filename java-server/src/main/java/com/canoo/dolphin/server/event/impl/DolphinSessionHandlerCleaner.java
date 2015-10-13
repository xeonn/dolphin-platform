package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final Iterable<DolphinContext> contexts = DolphinContextHandler.getContexts(se.getSession());
        if (contexts != null) {
            for (DolphinContext dolphinContext : contexts) {
                DolphinEventBusImpl.getInstance().unsubscribeSession(dolphinContext.getId());
            }
        }
    }
}
