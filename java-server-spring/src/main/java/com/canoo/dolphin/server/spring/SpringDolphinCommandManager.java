package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * {@link com.canoo.dolphin.server.container.DolphinCommandManager} implementation for a spring based application. This manager is autotically loaded by SPI.
 */
public class SpringDolphinCommandManager implements DolphinCommandManager {

    @Override
    public void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses) {
        for (Class<?> dolphinControllerClass : dolphinManagedClasses) {
            Object managedInstance = getContext(sc).getAutowireCapableBeanFactory().createBean(dolphinControllerClass);
            DolphinCommandRegistration.registerAllCommands(serverDolphin, dolphinControllerClass, managedInstance);
        }
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     * @param sc the servlet context
     * @return the spring context
     */
    private ApplicationContext getContext(ServletContext sc) {
        return WebApplicationContextUtils.getWebApplicationContext(sc);
    }

}
