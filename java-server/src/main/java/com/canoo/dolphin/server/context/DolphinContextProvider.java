package com.canoo.dolphin.server.context;

public interface DolphinContextProvider extends DolphinSessionProvider {

    DolphinContext getCurrentContext();
}
