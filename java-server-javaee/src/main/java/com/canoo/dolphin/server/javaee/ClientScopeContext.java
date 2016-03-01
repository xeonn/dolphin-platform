package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DefaultDolphinProvider;
import com.canoo.dolphin.server.context.DolphinSessionProvider;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientScopeContext implements Context {

    //see https://rpestano.wordpress.com/2013/06/30/cdi-custom-scope/
    // https://github.com/rmpestano/cdi-custom-scope/tree/master/custom-scope-extension/src/main/java/custom/scope/extension

    private final static String CLIENT_STORE_ATTRIBUTE = "DolphinPlatformCdiClientScopeStore";

    private DolphinSessionProvider dolphinSessionProvider;

    public ClientScopeContext() {
        dolphinSessionProvider = new DefaultDolphinProvider();
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        if (getLocalStore().containsKey(bean.getBeanClass())) {
            return (T) getLocalStore().get(bean.getBeanClass()).getInstance();
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        if (getLocalStore().containsKey(bean.getBeanClass())) {
            return (T) getLocalStore().get(bean.getBeanClass()).getInstance();
        } else {
            T instance = (T) bean.create(creationalContext);
            ClientScopeInstanceHolder<T> instanceHolder = new ClientScopeInstanceHolder(bean, creationalContext, instance);
            getLocalStore().put(bean.getBeanClass(), instanceHolder);
            return instance;
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ClientScoped.class;
    }

    public boolean isActive() {
        return getDolphinSession() != null;
    }

    private Map<Class<?>, ClientScopeInstanceHolder<?>> getLocalStore() {
        Map<Class<?>, ClientScopeInstanceHolder<?>> localStore = getDolphinSession().getAttribute(CLIENT_STORE_ATTRIBUTE);
        if(localStore == null) {
            localStore = Collections.synchronizedMap(new HashMap<Class<?>, ClientScopeInstanceHolder<?>>());
            getDolphinSession().setAttribute(CLIENT_STORE_ATTRIBUTE, localStore);
        }
        return localStore;
    }

    private DolphinSession getDolphinSession() {
        return dolphinSessionProvider.getDolphinSession();
    }
}