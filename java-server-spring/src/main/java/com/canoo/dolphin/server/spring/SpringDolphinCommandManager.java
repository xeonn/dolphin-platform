package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Set;

public class SpringDolphinCommandManager implements DolphinCommandManager {

    @Override
    public void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses) {
        for (Class<?> dolphinControllerClass : dolphinManagedClasses) {
            Object managedInstance = getContext(sc).getAutowireCapableBeanFactory().createBean(dolphinControllerClass);
            DolphinCommandRegistration.registerAllCommands(serverDolphin, dolphinControllerClass, managedInstance);
        }
    }

    private ApplicationContext getContext(ServletContext sc) {
        return WebApplicationContextUtils.getWebApplicationContext(sc);
    }

}
