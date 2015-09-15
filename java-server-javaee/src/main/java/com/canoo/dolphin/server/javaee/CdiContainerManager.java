package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.ContainerManager;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class CdiContainerManager implements ContainerManager {

    @Override
    public <T> T createManagedController(Class<T> controllerClass) {
//        return CDI.current().select(controllerClass).get();
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        Set<Bean<?>> beans = bm.getBeans(controllerClass);
        Bean<?> bean = bm.resolve(beans);
        Class<?> beanClass = bean.getBeanClass();
        CreationalContext<?> creationalContext = bm.createCreationalContext(bean);
        return (T) bm.getReference(bean, beanClass, creationalContext);
    }

    @Override
    public void destroyController(Object instance) {
        throw new RuntimeException("Not yet implemented");
    }
}
