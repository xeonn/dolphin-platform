package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.ContainerManager;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class CdiContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(ServletContext sc, Class<T> controllerClass) {
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        Bean<?> bean = bm.resolve(bm.getBeans(controllerClass));
        return (T) bm.getReference(bean, bean.getBeanClass(), bm.createCreationalContext(bean));
    }

    @Override
    public void destroyController(ServletContext sc, Object instance) {
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        Bean<?> bean = bm.resolve(bm.getBeans(instance.getClass()));
        bean.destroy(instance, bm.createCreationalContext(bean));
    }
}
