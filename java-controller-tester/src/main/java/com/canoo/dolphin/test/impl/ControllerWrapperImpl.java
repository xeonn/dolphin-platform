package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.test.ControllerWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ControllerWrapperImpl<C, M> implements ControllerWrapper<C, M> {

    private ControllerProxy<M> controllerProxy;

    private C controllerInstance;

    private InvocationHandler controllerInvocationHandler;

    public ControllerWrapperImpl(C controllerInstance, ControllerProxy<M> proxy) {
        this.controllerInstance = controllerInstance;
        this.controllerProxy = proxy;
        controllerInvocationHandler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(method.isAnnotationPresent(DolphinAction.class)) {
                    String actionName = method.getName();
                    if(method.getAnnotation(DolphinAction.class).value() != null && !method.getAnnotation(DolphinAction.class).value().isEmpty()) {
                        actionName = method.getAnnotation(DolphinAction.class).value();
                    }
                    return controllerProxy.invoke(actionName).get();
                } else {
                    return method.invoke(ControllerWrapperImpl.this.controllerInstance, args);
                }
            }
        };
    }

    @Override
    public C getController() {
        return (C) Proxy.newProxyInstance(controllerInstance.getClass().getClassLoader(),
                new Class<?>[]{controllerInstance.getClass()},
                controllerInvocationHandler);
    }

    @Override
    public M getModel() {
        return controllerProxy.getModel();
    }

    @Override
    public void destroy() {
        controllerProxy.destroy();
    }
}
