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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final ClientBeanManagerImpl clientBeanManager;

    private boolean killed = false;

    public ClientContextImpl(ClientDolphin clientDolphin) throws ExecutionException, InterruptedException {
        if(clientDolphin == null) {
            throw new IllegalArgumentException("clientDolphin must not be null!");
        }
        this.clientDolphin = clientDolphin;
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        final BeanRepository beanRepository = new BeanRepositoryImpl(clientDolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepositoryImpl(clientDolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        clientBeanManager.invoke(PlatformConstants.INIT_COMMAND_NAME).get();
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
        return getBeanManager().invoke(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME).handle((v, e) -> {
            if (e != null) {
                throw new RuntimeException(e);
            }
            @SuppressWarnings("unchecked")
            final T model = (T) bean.getModel();
            return new ControllerProxyImpl<>(bean.getControllerId(), model, this);
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
        return getBeanManager().invoke(PlatformConstants.DISCONNECT_COMMAND_NAME).thenAccept(v -> killed = true);
    }
}
