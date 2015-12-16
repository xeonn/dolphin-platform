package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.impl.ControllerProxyImpl;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.controller.ControllerHandler;
import com.canoo.dolphin.test.ControllerFactory;
import com.canoo.dolphin.test.ControllerWrapper;

public class ControllerFactoryImpl implements ControllerFactory {

    private ClientContext clientContext;

    private ControllerHandler controllerHandler;

    public ControllerFactoryImpl(ClientContext clientContext, ControllerHandler controllerHandler) {
        this.clientContext = clientContext;
        this.controllerHandler = controllerHandler;
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
            ControllerProxyImpl<M> proxy = (ControllerProxyImpl<M>) clientContext.createController(controllerName).get();
            String controllerId = proxy.getControllerId();
            C controllerInstance = controllerHandler.getControllerById(controllerId);
            return new ControllerWrapperImpl(controllerInstance, proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
