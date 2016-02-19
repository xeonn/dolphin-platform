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
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerInitalizationException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.State;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.StringUtil;
import org.opendolphin.core.client.ClientDolphin;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private State state = State.CREATED;

    private final List<WeakReference<ControllerProxy>> registeredWeakControllers;

    private final ControllerProxyFactory controllerProxyFactory;

    private final DolphinCommandHandler dolphinCommandHandler;

    public ClientContextImpl(ClientDolphin clientDolphin, ControllerProxyFactory controllerProxyFactory, DolphinCommandHandler dolphinCommandHandler, ClientPlatformBeanRepository platformBeanRepository, ClientBeanManagerImpl clientBeanManager) throws ExecutionException, InterruptedException {
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
        registeredWeakControllers = new CopyOnWriteArrayList<>();

        dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.INIT_COMMAND_NAME).thenAccept(v -> state = State.INITIALIZED).get();
    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null or empty!");
        }
        checkForInitializedState();

        CompletableFuture<ControllerProxy<T>> task = controllerProxyFactory.create(name);

        return task.handle((p, e) -> {
            if (e != null) {
                throw new ControllerInitalizationException(e);
            }
            registeredWeakControllers.add(new WeakReference<>(p));
            return p;
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
            for (WeakReference<ControllerProxy> destroyableRef : registeredWeakControllers) {
                try {
                    ControllerProxy destroyable = destroyableRef.get();
                    if(destroyable != null) {
                        destroyable.destroy().get();
                    }
                } catch (Exception e) {
                    //TODO
                } finally {
                    registeredWeakControllers.remove(destroyableRef);
                }
            }
            try {
                //TODO: Hack - When calling the PlatformConstants.DISCONNECT_COMMAND_NAME command the internal result listener in OD is never called and therefore the command handling will never be finished.
                // Currently I think that this is based on another problem: When calling the sisconnect on the JavaFX APplication.stop() method the Platform Tread will be stopped berfore the callback is called.
                state = State.DESTROYED;
                dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.DISCONNECT_COMMAND_NAME);
                result.complete(null);
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });

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
