package com.canoo.dolphin.server.container;

import org.opendolphin.core.server.ServerDolphin;

import java.util.Set;

public interface DolphinCommandManager {

    void initCommandsForSession(ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses);

}
