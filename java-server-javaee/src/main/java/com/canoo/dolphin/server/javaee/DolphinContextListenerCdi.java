package com.canoo.dolphin.server.javaee;

import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DolphinContextListener;
import com.canoo.dolphin.util.Assert;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;

import javax.enterprise.inject.spi.BeanManager;

@DolphinListener
public class DolphinContextListenerCdi implements DolphinContextListener {

    @Override
    public void contextCreated(DolphinSession dolphinSession) {

    }

    @Override
    public void contextDestroyed(DolphinSession dolphinSession) {
        Assert.requireNonNull(dolphinSession, "dolphinSession");
        BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
        ClientScopeContext clientContext = (ClientScopeContext) bm.getContext(ClientScoped.class);
        clientContext.destroy();
    }
}
