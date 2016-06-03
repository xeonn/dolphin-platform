package com.canoo.dolphin.server.context;

import javax.servlet.http.HttpSession;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public interface DolphinContextFactory {

    DolphinContext create(HttpSession httpSession, DolphinSessionListenerProvider dolphinSessionListenerProvider);

}
