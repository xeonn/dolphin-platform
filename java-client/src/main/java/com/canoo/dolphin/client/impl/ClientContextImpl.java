package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.Constants;
import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.impl.ControllerRegistryBean;
import org.opendolphin.core.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ClientContextImpl implements ClientContext {

    private final ClientDolphin clientDolphin;

    private final BeanRepository beanRepository;

    private final ClientBeanManagerImpl clientBeanManager;

    public ClientContextImpl(ClientDolphin clientDolphin) throws ExecutionException, InterruptedException {
        this.clientDolphin = clientDolphin;
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        beanRepository = new BeanRepository(clientDolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepository(clientDolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilder(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
        clientBeanManager.invoke(Constants.INIT_COMMAND_NAME).get();
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        final ControllerRegistryBean bean = getBeanManager().findAll(ControllerRegistryBean.class).get(0);
        bean.setControllerName(name);
        return getBeanManager().invoke(Constants.REGISTER_CONTROLLER_COMMAND_NAME).handle((v, e) -> {
            if(e != null) {
                throw new RuntimeException(e);
            }
            Object model = beanRepository.getBean(bean.getModelId());
            return new ControllerProxyImpl<T>(bean.getControllerid(), (T) model, this);
        });
    }

    @Override
    public ClientBeanManager getBeanManager() {
        return clientBeanManager;
    }

    @Override
    public void disconnect() {
        //How to disconnect in open dolphin?
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public ClientDolphin getDolphin() {
        return clientDolphin;
    }
}
