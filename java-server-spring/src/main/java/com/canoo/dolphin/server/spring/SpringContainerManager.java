/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import com.canoo.dolphin.server.context.DolphinContextListener;
import com.canoo.dolphin.util.Assert;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Spring specific implementation of the {@link ContainerManager} interface
 *
 * @author Hendrik Ebbers
 */
public class SpringContainerManager implements ContainerManager {

    private ServletContext servletContext;

    @Override
    public void init(ServletContext servletContext) {
        if(servletContext == null) {
            throw new IllegalArgumentException("servletContext must not be null!");
        }
        this.servletContext = servletContext;
        WebApplicationContext context = getContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.addBeanPostProcessor(SpringModelInjector.getInstance());
    }

    @Override
    public <T> T createManagedController(final Class<T> controllerClass, final ModelInjector modelInjector) {
        Assert.requireNonNull(controllerClass, "controllerClass");
        Assert.requireNonNull(modelInjector, "modelInjector");
        // SpringBeanAutowiringSupport kann man auch nutzen
        WebApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        SpringModelInjector.getInstance().prepare(controllerClass, modelInjector);
        return beanFactory.createBean(controllerClass);
    }

    @Override
    public <T extends DolphinContextListener> T createContextListener(Class<T> listenerClass) {
        Assert.requireNonNull(listenerClass, "listenerClass");
        WebApplicationContext context = getContext();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        return beanFactory.createBean(listenerClass);
    }

    @Override
    public void destroyController(Object instance, Class controllerClass) {
        Assert.requireNonNull(instance, "instance");
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    private WebApplicationContext getContext() {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
