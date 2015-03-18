package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

public class CdiDolphinCommandManager implements DolphinCommandManager {

    @Override
    public void initCommandsForSession(ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses) {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
        for (Class<?> cls : dolphinManagedClasses) {
            Bean<?> bean = createBean(beanManager, cls);
            Object reference = beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
            DolphinCommandRegistration.registerAllComands(serverDolphin, cls, reference);
        }
    }

    private <T> Bean<T> createBean(BeanManager beanManager, Class<T> cls) {
        return new BeanBuilder<T>(beanManager)
                .passivationCapable(true)
                .beanClass(cls)
                .scope(SessionScoped.class)
                .create();
    }
}
