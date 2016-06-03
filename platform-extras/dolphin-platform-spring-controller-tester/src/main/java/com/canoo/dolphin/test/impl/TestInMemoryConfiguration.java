package com.canoo.dolphin.test.impl;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerDolphinFactory;

public class TestInMemoryConfiguration {

    final ClientDolphin clientDolphin = new ClientDolphin();

    final DefaultServerDolphin serverDolphin = (DefaultServerDolphin) ServerDolphinFactory.create();

    public TestInMemoryConfiguration() {

        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));

        InMemoryClientConnector inMemoryClientConnector = new InMemoryClientConnector(clientDolphin, serverDolphin.getServerConnector());
        inMemoryClientConnector.setSleepMillis(100);
        clientDolphin.setClientConnector(inMemoryClientConnector);
        clientDolphin.getClientConnector().setStrictMode(false);
    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    public DefaultServerDolphin getServerDolphin() {
        return serverDolphin;
    }
}
