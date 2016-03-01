package com.canoo.dolphin.server.context;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public interface DolphinContextProvider extends DolphinSessionProvider{

    DolphinContext getCurrentContext();
}
