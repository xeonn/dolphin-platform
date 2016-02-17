/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.client;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import org.opendolphin.LogConfig;
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
        LogConfig.logOnLevel(clientConfiguration.getDolphinLogLevel());
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
