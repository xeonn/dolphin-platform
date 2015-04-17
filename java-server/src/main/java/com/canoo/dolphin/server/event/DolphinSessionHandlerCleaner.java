package com.canoo.dolphin.server.event;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinSessionHandlerCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DolphinEventBusImpl.getInstance().unregisterHandlersForCurrentDolphinSession();
    }
}
