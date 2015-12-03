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

import com.canoo.dolphin.server.container.ModelInjector;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Implements a CDI Lifecylce for Dolphin Platform Controllers
 *
 * @param <T> the class of the bean instance
 *
 * @author Hendrik Ebbers
 */
public class DolphinPlatformContextualLifecycle<T> implements ContextualLifecycle<T> {

    private final InjectionTarget<T> injectionTarget;

    private final ModelInjector modelInjector;

    public DolphinPlatformContextualLifecycle(InjectionTarget<T> injectionTarget, ModelInjector modelInjector) {
        this.injectionTarget = injectionTarget;
        this.modelInjector = modelInjector;
    }

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
}
