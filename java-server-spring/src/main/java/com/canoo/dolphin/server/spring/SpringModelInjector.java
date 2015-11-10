package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.container.ModelInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class SpringModelInjector extends InstantiationAwareBeanPostProcessorAdapter {

    private ThreadLocal<ModelInjector> currentModelInjector = new ThreadLocal<>();

    private ThreadLocal<Class> currentControllerClass = new ThreadLocal<>();

    private static SpringModelInjector instance = new SpringModelInjector();

    public void prepair(Class controllerClass, ModelInjector injector) {
        currentControllerClass.set(controllerClass);
        currentModelInjector.set(injector);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        Class controllerClass = currentControllerClass.get();
        if (controllerClass != null && controllerClass.isAssignableFrom(bean.getClass())) {
            ModelInjector modelInjector = currentModelInjector.get();
            if (modelInjector != null) {
                modelInjector.inject(bean);
            }
        }
        return true;
    }

    public static SpringModelInjector getInstance() {
        return instance;
    }
}
