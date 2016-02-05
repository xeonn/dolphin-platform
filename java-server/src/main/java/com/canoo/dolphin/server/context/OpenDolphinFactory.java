package com.canoo.dolphin.server.context;

import org.opendolphin.core.server.DefaultServerDolphin;

/**
 * Created by hendrikebbers on 05.02.16.
 */
public interface OpenDolphinFactory {

    DefaultServerDolphin create();
}
