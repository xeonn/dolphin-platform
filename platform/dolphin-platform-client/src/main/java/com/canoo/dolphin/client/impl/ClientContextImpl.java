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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.ControllerInitalizationException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.State;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.opendolphin.core.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private State state = State.CREATED;

    private final ControllerProxyFactory controllerProxyFactory;

    private final DolphinCommandHandler dolphinCommandHandler;

    public ClientContextImpl(ClientConfiguration clientConfiguration, ClientDolphin clientDolphin, ControllerProxyFactory controllerProxyFactory, DolphinCommandHandler dolphinCommandHandler, ClientPlatformBeanRepository platformBeanRepository, ClientBeanManagerImpl clientBeanManager) throws ExecutionException, InterruptedException {
        Assert.requireNonNull(clientDolphin, "clientDolphin");
        Assert.requireNonNull(controllerProxyFactory, "controllerProxyFactory");
        Assert.requireNonNull(dolphinCommandHandler, "dolphinCommandHandler");
        Assert.requireNonNull(platformBeanRepository, "platformBeanRepository");
        Assert.requireNonNull(clientBeanManager, "clientBeanManager");
        this.clientDolphin = clientDolphin;
        this.controllerProxyFactory = controllerProxyFactory;
        this.dolphinCommandHandler = dolphinCommandHandler;
        this.platformBeanRepository = platformBeanRepository;
        this.clientBeanManager = clientBeanManager;
        try {
            dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.INIT_CONTEXT_COMMAND_NAME).handle((v, e) -> {
                if (e != null) {
                    state = State.DESTROYED;
                    throw new ClientInitializationException("Can't call init action!");
                } else {
                    state = State.INITIALIZED;
                }
                return null;
            }).get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new ClientInitializationException("Can not connect to server!", e);
        }
    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        Assert.requireNonBlank(name, "name");
        checkForInitializedState();

        return controllerProxyFactory.<T>create(name).handle((c, e) -> {
            if (e != null) {
                throw new ControllerInitalizationException(e);
            }
            return c;
        });
    }

    @Override
    public synchronized ClientBeanManager getBeanManager() {
        checkForInitializedState();
        return clientBeanManager;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        checkForInitializedState();
        state = State.DESTROYING;
        clientDolphin.stopPushListening();
        final CompletableFuture<Void> result = new CompletableFuture<>();

        Executors.newSingleThreadExecutor().execute(() -> {
                    state = State.DESTROYED;
                    dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.DESTROY_CONTEXT_COMMAND_NAME).handle((v, t) -> {
                        if (t != null) {
                            result.completeExceptionally(new DolphinRemotingException("Can't disconnect", t));
                        } else {
                            result.complete(null);
                        }
                        return null;
                    });
                }
        );

        return result;
    }

    private void checkForInitializedState() {
        switch (state) {
            case CREATED:
                throw new IllegalStateException("The client is initialized!");
            case DESTROYED:
                throw new IllegalStateException("The client is disconnected!");
            case DESTROYING:
                throw new IllegalStateException("The client is disconnecting!");
        }
    }
}
