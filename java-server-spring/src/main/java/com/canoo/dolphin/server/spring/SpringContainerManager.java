package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DolphinContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class SpringContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(Class<T> controllerClass) {
        ApplicationContext context = getContext();
        return context.getAutowireCapableBeanFactory().createBean(controllerClass);
    }

    @Override
    public void destroyController(Object instance) {
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     * @return the spring context
     */
    private ApplicationContext getContext() {
        return WebApplicationContextUtils.getWebApplicationContext(DolphinContext.getCurrentContext().getServletContext());
    }
}
