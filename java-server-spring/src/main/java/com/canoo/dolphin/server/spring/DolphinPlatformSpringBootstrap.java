package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.TaskExecutor;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.event.impl.TaskExecutorImpl;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import org.opendolphin.core.server.ServerDolphin;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Basic Bootstrap for Spring based application. The bootstrap automatically starts the dolphin platform bootstrap.
 */
@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        new DolphinPlatformBootstrap().onStartup(servletContext);
    }

    /**
     * Method to create a spring managed {@link BeanManagerImpl} instance in session scope.
     * @return the instance
     */
    @Bean
    @Scope("session")
    protected BeanManager createManager() {
        return DolphinContext.getCurrentContext().getBeanManager();
    }

    /**
     * Method to create a spring managed {@link org.opendolphin.core.server.ServerDolphin} instance in session scope.
     * @return the instance
     */
    @Bean
    @Scope("session")
    protected ServerDolphin createDolphin() {
        return DolphinContext.getCurrentContext().getDolphin();
    }

    @Bean
    @Scope("session")
    protected TaskExecutor createTaskExecutor() {
        return TaskExecutorImpl.getInstance();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    protected DolphinEventBus createEventBus() {
        return DolphinEventBusImpl.getInstance();
    }

}
