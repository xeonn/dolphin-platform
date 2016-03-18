package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;

public interface DolphinContextListener {

    void contextCreated(DolphinSession dolphinSession);

    void contextDestroyed(DolphinSession dolphinSession);

}
