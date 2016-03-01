package com.canoo.dolphin.server.context;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public class DefaultDolphinContextProvider implements DolphinContextProvider {

    @Override
    public DolphinContext getCurrentContext() {
        return DolphinContextHandler.getCurrentContext();
    }
}
