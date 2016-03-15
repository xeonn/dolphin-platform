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

import com.canoo.dolphin.server.container.ModelInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

/**
 * A Dolphin Platform specific implementation of {@link InstantiationAwareBeanPostProcessorAdapter} that injects
 * the model instance in Dolphin Platform controllers.
 *
 * @author Hendrik Ebbers
 * @since 0.7
 */
public class SpringModelInjector extends InstantiationAwareBeanPostProcessorAdapter {

    private ThreadLocal<ModelInjector> currentModelInjector = new ThreadLocal<>();

    private ThreadLocal<Class> currentControllerClass = new ThreadLocal<>();

    private static SpringModelInjector instance = new SpringModelInjector();

    public void prepare(Class controllerClass, ModelInjector injector) {
        if(controllerClass == null) {
            throw new IllegalArgumentException("controllerClass must not be null!");
        }
        if(injector == null) {
            throw new IllegalArgumentException("injector must not be null!");
        }
        currentControllerClass.set(controllerClass);
        currentModelInjector.set(injector);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if(bean == null) {
            throw new IllegalArgumentException("bean must not be null!");
        }
        Class controllerClass = currentControllerClass.get();
        if (controllerClass != null && controllerClass.isAssignableFrom(bean.getClass())) {
            ModelInjector modelInjector = currentModelInjector.get();
            if (modelInjector != null) {
                modelInjector.inject(bean);
            }
            currentControllerClass.set(null);
            currentModelInjector.set(null);
        }
        return true;
    }

    public static SpringModelInjector getInstance() {
        return instance;
    }
}
