/*
 * Copyright 2015 Canoo Engineering AG.
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
import com.canoo.dolphin.server.context.DolphinContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(final Class<T> controllerClass, final ModelInjector modelInjector) {
        ApplicationContext context = getContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        beanFactory.addBeanPostProcessor(new InstantiationAwareBeanPostProcessorAdapter() {
            @Override
            public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
                modelInjector.inject(bean);
                return true;
            }
        });
        return beanFactory.createBean(controllerClass);
    }

    @Override
    public void destroyController(Object instance) {
        ApplicationContext context = getContext();
        context.getAutowireCapableBeanFactory().destroyBean(instance);
    }

    /**
     * Returns the Spring {@link org.springframework.context.ApplicationContext} for the current {@link javax.servlet.ServletContext}
     *
     * @return the spring context
     */
    private ApplicationContext getContext() {
        return WebApplicationContextUtils.getWebApplicationContext(DolphinContext.getCurrentContext().getServletContext());
    }
}
