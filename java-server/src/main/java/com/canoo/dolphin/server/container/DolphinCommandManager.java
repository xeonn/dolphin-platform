package com.canoo.dolphin.server.container;

import org.opendolphin.core.server.ServerDolphin;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * Basic interfaces that inits the dolphin controllers for a session.
 */
public interface DolphinCommandManager {

    /**
     * This method creates managed instances of the given dolphin controller classes and registers all actions of the
     * instances as dolphin commands.
     * @param sc the current servlet context
     * @param serverDolphin the current dolphin
     * @param dolphinManagedClasses list of all classes that should be managed
     */
    void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses);

}
