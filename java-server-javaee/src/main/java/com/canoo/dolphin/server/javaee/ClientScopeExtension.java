package com.canoo.dolphin.server.javaee;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import java.io.Serializable;

public class ClientScopeExtension implements Extension, Serializable {

    public void addScope(@Observes final BeforeBeanDiscovery event) {
        event.addScope(ClientScope.class, true, false);
    }

    public void registerContext(@Observes final AfterBeanDiscovery event) {
        event.addContext(new ClientScopeContext());
    }
}