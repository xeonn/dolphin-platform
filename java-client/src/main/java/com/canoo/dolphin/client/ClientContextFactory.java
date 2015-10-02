package com.canoo.dolphin.client;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.comm.JsonCodec;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ClientContextFactory {

    private ClientContextFactory() {
    }

    public static CompletableFuture<ClientContext> connect(ClientConfiguration clientConfiguration) {
        final CompletableFuture<ClientContext> result = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                final ClientDolphin dolphin = new ClientDolphin();
                dolphin.setClientModelStore(new ClientModelStore(dolphin));
                final HttpClientConnector clientConnector = new HttpClientConnector(dolphin, clientConfiguration.getServerEndpoint());
                clientConnector.setCodec(new JsonCodec());
                clientConnector.setUiThreadHandler(clientConfiguration.getUiThreadHandler());
                dolphin.setClientConnector(clientConnector);
                final ClientContext clientContext = new ClientContextImpl(dolphin);
                dolphin.startPushListening(PlatformConstants.POLL_COMMAND_NAME, PlatformConstants.RELEASE_COMMAND_NAME);
                clientConfiguration.getUiThreadHandler().executeInsideUiThread(() -> result.complete(clientContext));
            } catch (Exception e) {
                throw new ClientInitializationException(e);
            }
        });
        return result;
    }

}
