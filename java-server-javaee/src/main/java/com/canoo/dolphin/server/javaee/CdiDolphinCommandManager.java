package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import java.util.Set;

public class CdiDolphinCommandManager implements DolphinCommandManager {

    @Override
    public void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses) {
        for (Class<?> cls : dolphinManagedClasses) {
            Object reference = getManagedInstance(cls);
            DolphinCommandRegistration.registerAllCommands(serverDolphin, cls, reference);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getManagedInstance(Class<T> clazz) {
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        Bean<?> bean = bm.resolve(bm.getBeans(clazz));
        return (T) bm.getReference(bean, bean.getBeanClass(), bm.createCreationalContext(bean));
    }

}
