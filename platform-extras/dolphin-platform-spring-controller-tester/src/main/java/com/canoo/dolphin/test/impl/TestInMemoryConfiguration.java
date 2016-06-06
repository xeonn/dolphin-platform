package com.canoo.dolphin.test.impl;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerDolphinFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestInMemoryConfiguration {

    final ClientDolphin clientDolphin = new ClientDolphin();

    final DefaultServerDolphin serverDolphin = (DefaultServerDolphin) ServerDolphinFactory.create();

    private final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();

    public TestInMemoryConfiguration() {

        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));

        InMemoryClientConnector inMemoryClientConnector = new InMemoryClientConnector(clientDolphin, serverDolphin.getServerConnector());

        inMemoryClientConnector.setSleepMillis(0);
        clientDolphin.setClientConnector(inMemoryClientConnector);
        clientDolphin.getClientConnector().setStrictMode(false);

        inMemoryClientConnector.setUiThreadHandler(new UiThreadHandler() {

            @Override
            public void executeInsideUiThread(Runnable runnable) {
                clientExecutor.execute(runnable);
            }
        });
    }

    public ExecutorService getClientExecutor() {
        return clientExecutor;
    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    public DefaultServerDolphin getServerDolphin() {
        return serverDolphin;
    }
}
