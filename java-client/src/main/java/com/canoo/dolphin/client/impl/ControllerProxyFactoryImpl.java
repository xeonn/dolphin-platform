package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.impl.InternalAttributesBean;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;

public class ControllerProxyFactoryImpl implements ControllerProxyFactory {

    private final ClientPlatformBeanRepository platformBeanRepository;

    private final DolphinCommandHandler dolphinCommandHandler;

    private final ClientDolphin clientDolphin;

    public ControllerProxyFactoryImpl(ClientPlatformBeanRepository platformBeanRepository, DolphinCommandHandler dolphinCommandHandler, ClientDolphin clientDolphin) {
        this.platformBeanRepository = platformBeanRepository;
        this.dolphinCommandHandler = dolphinCommandHandler;
        this.clientDolphin = clientDolphin;
    }

    @Override
    public <T> CompletableFuture<ControllerProxy<T>> create(String name) {
        Assert.requireNonBlank(name, "name");
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        bean.setControllerName(name);

        return dolphinCommandHandler.invokeDolphinCommand(PlatformConstants.REGISTER_CONTROLLER_COMMAND_NAME).thenApply((v) -> {
            return new ControllerProxyImpl<>(bean.getControllerId(), (T) bean.getModel(), clientDolphin, platformBeanRepository);
        });
    }
}
