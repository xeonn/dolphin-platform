package com.canoo.dolphin.server.context;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by hendrikebbers on 15.09.15.
 */
public class DolphinContextCleaner implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DolphinContextHandler.removeAllContextsInSession(se.getSession());
    }
}
