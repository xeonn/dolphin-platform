package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.ModelInjector;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

public class DolphinPlatformContextualLifecycle<T> implements ContextualLifecycle<T> {

    final InjectionTarget<T> injectionTarget;

    final ModelInjector modelInjector;

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
