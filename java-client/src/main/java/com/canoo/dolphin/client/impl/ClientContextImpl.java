package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.Constants;
import com.canoo.dolphin.client.ClientBeanManagerImpl;
import com.canoo.dolphin.client.v2.ClientBeanManager;
import com.canoo.dolphin.client.v2.ClientContext;
import com.canoo.dolphin.client.v2.ControllerProxy;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.v2.ControllerRegistryBean;
import org.opendolphin.core.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ClientContextImpl implements ClientContext {

    private ClientDolphin clientDolphin;

    private ClientBeanManagerImpl clientBeanManager;

    public ClientContextImpl(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        final EventDispatcher dispatcher = new ClientEventDispatcher(clientDolphin);
        final BeanRepository beanRepository = new BeanRepository(clientDolphin, dispatcher);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(clientDolphin);
        final ClassRepository classRepository = new ClassRepository(clientDolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(clientDolphin, classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new BeanBuilder(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        clientBeanManager = new ClientBeanManagerImpl(beanRepository, beanBuilder, clientDolphin);
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        ControllerRegistryBean bean = getBeanManager().findAll(ControllerRegistryBean.class).get(0);
        bean.getControllerName().set(name);
        return getBeanManager().send(Constants.REGISTER_CONTROLLER_COMMAND_NAME).handle((v, e) -> {
            if(e != null) {
                throw new RuntimeException(e);
            }
            return bean.getControllerid().get();
        }).handle((id, e) -> {
            if(e != null) {
                throw new RuntimeException(e);
            }
            return new ControllerProxyImpl<T>(id, this);
        });
    }

    @Override
    public ClientBeanManager getBeanManager() {
        return clientBeanManager;
    }

    @Override
    public void disconnect() {
        //How to disconnect in open dolphin?
        throw new RuntimeException("Not yet implemented!");
    }

    @Override
    public ClientDolphin getDolphin() {
        return clientDolphin;
    }
}
