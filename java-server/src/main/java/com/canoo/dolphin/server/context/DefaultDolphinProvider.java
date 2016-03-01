package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public class DefaultDolphinProvider implements DolphinContextProvider, DolphinSessionProvider {

    @Override
    public DolphinContext getCurrentContext() {
        return DolphinContextHandler.getCurrentContext();
    }

    @Override
    public DolphinSession getDolphinSession() {
        return getCurrentContext().getDolphinSession();
    }
}
