package com.canoo.dolphin.client;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.comm.JsonCodec;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Factory to create a {@link ClientContext}. Normally you will create a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #connect(ClientConfiguration)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the Dolphin Platform server endpoint.
 */
public class ClientContextFactory {

    private ClientContextFactory() {
    }

    /**
     * Create a {@link ClientContext} based on the given configuration. This method doesn't block and returns a
     * {@link CompletableFuture} to receive its result. If the {@link ClientContext} can't be created the
     * {@link CompletableFuture#get()} will throw a {@link ClientInitializationException}.
     * @param clientConfiguration the configuration
     * @return the future
     */
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
