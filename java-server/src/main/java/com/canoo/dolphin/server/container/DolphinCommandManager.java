package com.canoo.dolphin.server.container;

import org.opendolphin.core.server.ServerDolphin;

import javax.servlet.ServletContext;
import java.util.Set;

public interface DolphinCommandManager {

    void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses);

}
