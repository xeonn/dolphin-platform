package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import com.canoo.dolphin.server.container.DolphinCommandRegistration;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.opendolphin.core.server.ServerDolphin;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Set;

public class CdiDolphinCommandManager implements DolphinCommandManager {

    public CdiDolphinCommandManager() {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
        ServerDolphinBean dolphinBean = new ServerDolphinBean();
    }

    @Override
    public void initCommandsForSession(ServletContext sc, ServerDolphin serverDolphin, Set<Class<?>> dolphinManagedClasses) {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
        for (Class<?> cls : dolphinManagedClasses) {
            Bean<?> bean = getBean(beanManager, cls);
            Object reference = beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
            DolphinCommandRegistration.registerAllCommands(serverDolphin, cls, reference);
        }
    }

    private static HashMap<Class<?>, Bean<?>> beanCache;

    public static synchronized Bean<?> getBean(BeanManager beanManager, Class<?> cls) {
        if (beanCache == null) {
            beanCache = new HashMap<>();
        }
        if (!beanCache.containsKey(cls)) {
            Bean<?> bean = new BeanBuilder(beanManager)
                    .beanClass(cls)
                    .scope(SessionScoped.class)
                    .create();
            beanCache.put(cls, bean);
        }
        return beanCache.get(cls);
    }
}
