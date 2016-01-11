/*
 * Copyright 2015 Canoo Engineering AG.
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

import com.canoo.dolphin.client.*;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import org.opendolphin.StringUtil;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private State state = State.CREATED;

    private List<WeakReference<ControllerProxy>> registeredWeakControllers;

    public ClientContextImpl(ClientDolphin clientDolphin) throws ExecutionException, InterruptedException {
        if (clientDolphin == null) {
            throw new IllegalArgumentException("clientDolphin must not be null!");
        }
        this.clientDolphin = clientDolphin;
        registeredWeakControllers = new CopyOnWriteArrayList<>();
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        final BeanRepository beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        platformBeanRepository = new ClientPlatformBeanRepository(clientDolphin, beanRepository, dispatcher);

        invokeDolphinCommand(PlatformConstants.INIT_COMMAND_NAME).thenAccept(v -> state = State.INITIALIZED).get();
    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null or empty!");
        }
        checkForInitializedState();
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        bean.setControllerName(name);
        return invokeDolphinCommand(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME).handle((v, e) -> {
            if (e != null) {
                throw new RuntimeException(e);
            }
            @SuppressWarnings("unchecked")
            final T model = (T) bean.getModel();
            ControllerProxyImpl controllerProxy = new ControllerProxyImpl<>(bean.getControllerId(), model, clientDolphin, platformBeanRepository);
            registeredWeakControllers.add(new WeakReference<>(controllerProxy));
            return controllerProxy;
        });
    }

    @Override
    public synchronized ClientBeanManager getBeanManager() {
        checkForInitializedState();
        return clientBeanManager;
    }

    private synchronized CompletableFuture<Void> invokeDolphinCommand(String command) {
        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientDolphin.send(command, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                result.complete(null);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return result;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        checkForInitializedState();
        state = State.DESTROYING;

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
                invokeDolphinCommand(PlatformConstants.DISCONNECT_COMMAND_NAME).get();
                state = State.DESTROYED;
                //result.complete(null);
            } catch (Exception e) {
                result.obtrudeException(e);
            }
        });

        //TODO: Hack - When calling the PlatformConstants.DISCONNECT_COMMAND_NAME command the internal result listener in OD is never called and therefore the command handling will never be finished.
        result.complete(null);

        return result;
    }

    private void checkForInitializedState() {
        if (state.equals(State.CREATED)) {
            throw new IllegalStateException("The client is initialized!");
        }
        if (state.equals(State.DESTROYED)) {
            throw new IllegalStateException("The client is disconnected!");
        }
        if (state.equals(State.DESTROYING)) {
            throw new IllegalStateException("The client is disconnecting!");
        }
    }
}
