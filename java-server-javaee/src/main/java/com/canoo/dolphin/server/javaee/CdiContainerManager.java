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
package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.container.ModelInjector;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.bean.BeanBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class CdiContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector) {
        try {
            controllerClass = (Class<T>) Class.forName("com.canoo.dolphin.todo.server.ToDoController");
            BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
            //see https://github.com/rmannibucau/cdi-light-config/blob/77603cb364667bad493ad8bd115e3da1564335c0/src/main/java/com/github/rmannibucau/cdi/configuration/LightConfigurationExtension.java
            final Bean<Object> bean = new BeanBuilder<Object>(bm)
                    .passivationCapable(true) // you can add some logic it to check it or configure it
                    .beanClass(controllerClass)
                    .scope(SessionScoped.class) // can be configurable
            .create();
            Class<?> beanClass = bean.getBeanClass();
            CreationalContext<?> creationalContext = bm.createCreationalContext(bean);
            return (T) bm.getReference(bean, beanClass, creationalContext);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroyController(Object instance) {
        throw new RuntimeException("Not yet implemented");
    }
}
