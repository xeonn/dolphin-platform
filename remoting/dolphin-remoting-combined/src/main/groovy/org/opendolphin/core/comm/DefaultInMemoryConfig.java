package org.opendolphin.core.comm;

import org.opendolphin.LogConfig;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerDolphinFactory;

/**
 * Base class for running a client and server dolphin inside the same VM.
 * <p>
 * Subclasses JavaFxInMemoryConfig and SwingInMemoryConfig additionally set the threading model
 * as appropriate for the UI (JavaFX or Swing, respectively.)
 */
public class DefaultInMemoryConfig {

    private final ClientDolphin clientDolphin;

    private final ServerDolphin serverDolphin;

    private final InMemoryClientConnector clientConnector;

    public DefaultInMemoryConfig() {
        LogConfig.logCommunication();

        clientDolphin = new ClientDolphin();
        serverDolphin = ServerDolphinFactory.create();

        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
        clientConnector = new InMemoryClientConnector(clientDolphin, serverDolphin.getServerConnector());
        clientDolphin.setClientConnector(clientConnector);

        clientConnector.setSleepMillis(100);

    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    public ServerDolphin getServerDolphin() {
        return serverDolphin;
    }

    public InMemoryClientConnector getClientConnector() {
        return clientConnector;
    }
}
