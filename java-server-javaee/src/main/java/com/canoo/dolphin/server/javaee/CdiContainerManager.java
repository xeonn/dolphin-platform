package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.ContainerManager;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class CdiContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(Class<T> controllerClass) {
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        Bean<?> bean = bm.resolve(bm.getBeans(controllerClass));
        return (T) bm.getReference(bean, bean.getBeanClass(), bm.createCreationalContext(bean));
    }

    @Override
    public void destroyController(Object instance) {
        throw new RuntimeException("Not yet implemented");
    }
}
