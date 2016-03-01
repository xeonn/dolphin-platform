package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public interface DolphinSessionProvider {

    DolphinSession getDolphinSession();
}
