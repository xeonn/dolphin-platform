package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.context.DefaultDolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinContextProvider;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import java.lang.annotation.Annotation;

public class ClientScopeContext implements Context {

    private DolphinContextProvider dolphinContextProvider;

    public ClientScopeContext() {
        dolphinContextProvider = new DefaultDolphinContextProvider();
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
       return null;
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
       return null;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ClientScope.class;
    }

    public boolean isActive() {
        return true;
    }

}