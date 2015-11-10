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
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.ServletContext;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class CdiContainerManager implements ContainerManager {

    @Override
    public void init(ServletContext servletContext) {
    }

    @Override
    public <T> T createManagedController(final Class<T> controllerClass, final ModelInjector modelInjector) {
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        AnnotatedType annotatedType = bm.createAnnotatedType(controllerClass);
        final InjectionTarget<T> injectionTarget = bm.createInjectionTarget(annotatedType);
        final Bean<T> bean = new BeanBuilder<T>(bm)
                .beanClass(controllerClass)
                .scope(Dependent.class)
                .beanLifecycle(new ContextualLifecycle<T>() {
                    @Override
                    public T create(Bean<T> bean, CreationalContext<T> creationalContext) {
                        T instance = injectionTarget.produce(creationalContext);
                        modelInjector.inject(instance);
                        injectionTarget.inject(instance, creationalContext);
                        injectionTarget.postConstruct(instance);
                        return instance;
                    }

                    @Override
                    public void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext) {
                        injectionTarget.preDestroy(instance);
                        creationalContext.release();
                    }
                })
                .create();
        Class<?> beanClass = bean.getBeanClass();
        CreationalContext<?> creationalContext = bm.createCreationalContext(bean);
        T instance = (T) bm.getReference(bean, beanClass, creationalContext);
        return instance;
    }

    @Override
    public void destroyController(Object instance) {
        throw new RuntimeException("Not yet implemented");
    }
}
