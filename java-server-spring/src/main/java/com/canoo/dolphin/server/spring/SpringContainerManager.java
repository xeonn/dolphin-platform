package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ContainerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class SpringContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(ServletContext sc, Class<T> controllerClass) {
        ApplicationContext context = getContext(sc);
        return context.getAutowireCapableBeanFactory().createBean(controllerClass);
    }

    @Override
    public void destroyController(ServletContext sc, Object instance) {
        ApplicationContext context = getContext(sc);
        context.getAutowireCapableBeanFactory().destroyBean(instance);
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
