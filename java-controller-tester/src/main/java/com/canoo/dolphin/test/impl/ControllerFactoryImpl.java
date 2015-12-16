package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.test.ControllerFactory;
import com.canoo.dolphin.test.ControllerWrapper;

public class ControllerFactoryImpl implements ControllerFactory {

    private ClientContext clientContext;

    public ControllerFactoryImpl(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public <C, M> ControllerWrapper<C, M> create(Class<C> controllerClass) {
        String controllerName = controllerClass.getName();
        if (controllerClass.isAnnotationPresent(DolphinController.class) &&
                controllerClass.getAnnotation(DolphinController.class).value() != null
                && !controllerClass.getAnnotation(DolphinController.class).value().isEmpty()) {
            controllerName = controllerClass.getAnnotation(DolphinController.class).value();
        }

        try {
            ControllerProxy<M> proxy = (ControllerProxy<M>) clientContext.createController(controllerName).get();
            C controllerInstance = null;
            return new ControllerWrapperImpl(controllerInstance, proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
