package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.AbstractConnector;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerDolphinFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestInMemoryConfiguration {

    final ClientDolphin clientDolphin = new ClientDolphin();

    final DefaultServerDolphin serverDolphin = (DefaultServerDolphin) ServerDolphinFactory.create();

    private final ExecutorService clientExecutor = Executors.newSingleThreadExecutor();

    public TestInMemoryConfiguration() {

        UiThreadHandler threadHandler = new UiThreadHandler() {

            @Override
            public void executeInsideUiThread(Runnable runnable) {
                clientExecutor.execute(runnable);
            }
        };

        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));

        ClientConnector inMemoryClientConnector = new AbstractConnector(new BlindCommandBatcher(), clientDolphin, threadHandler) {

            @Override
            public void send(Command command, OnFinishedHandler callback) {
                super.send(command, callback);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public List<Command> transmit(List<Command> commands) {
                List<Command> result = new ArrayList<>();
                for(Command c : commands) {
                    result.addAll(getServerDolphin().getServerConnector().receive(c));
                }
                return result;
            }

        };
        clientDolphin.setClientConnector(inMemoryClientConnector);
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
