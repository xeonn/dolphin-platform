package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.context.DolphinContext;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public interface DolphinContextProvider {

    DolphinContext getCurrentContext();
}
