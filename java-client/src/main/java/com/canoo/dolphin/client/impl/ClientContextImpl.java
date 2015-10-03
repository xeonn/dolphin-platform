package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.impl.ControllerRegistryBean;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import org.opendolphin.StringUtil;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private final BeanRepository beanRepository;

    private boolean killed = false;

    public ClientContextImpl(ClientDolphin clientDolphin) throws ExecutionException, InterruptedException {
        if(clientDolphin == null) {
            throw new IllegalArgumentException("clientDolphin must not be null!");
        }
        this.clientDolphin = clientDolphin;
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        invokeDolphinCommand(PlatformConstants.INIT_COMMAND_NAME).get();
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        if(StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("name must not be null or empty!");
        }
        if(killed) {
            throw new IllegalStateException("The client is disconnected!");
        }
        final ControllerRegistryBean bean = getBeanManager().findAll(ControllerRegistryBean.class).get(0);
        bean.setControllerName(name);
        return invokeDolphinCommand(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME).handle((v, e) -> {
            if (e != null) {
                throw new RuntimeException(e);
            }
            @SuppressWarnings("unchecked")
            final T model = (T) bean.getModel();
            return new ControllerProxyImpl<>(bean.getControllerId(), model, this, clientDolphin, beanRepository);
        });
    }

    @Override
    public ClientBeanManager getBeanManager() {
        if(killed) {
            throw new IllegalStateException("The client is disconnected!");
        }
        return clientBeanManager;
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        if(killed) {
            throw new IllegalStateException("The client is disconnected!");
        }
        return invokeDolphinCommand(PlatformConstants.DISCONNECT_COMMAND_NAME).thenAccept(v -> killed = true);
    }


    private CompletableFuture<Void> invokeDolphinCommand(String command) {
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


}
