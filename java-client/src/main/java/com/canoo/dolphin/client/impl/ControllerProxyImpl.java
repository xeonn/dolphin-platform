package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.Constants;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.ControllerDestroyBean;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private String controllerId;

    private ClientContext context;

    private T model;

    public ControllerProxyImpl(String controllerId, T model, ClientContext context) {
        this.controllerId = controllerId;
        this.model = model;
        this.context = context;
    }

    @Override
    public T getModel() {
       return model;
    }

    @Override
    public CompletableFuture<Void> call(String actionName) {
        ControllerActionCallBean bean = context.getBeanManager().findAll(ControllerActionCallBean.class).get(0);
        bean.getControllerid().set(controllerId);
        bean.getActionName().set(actionName);
        return context.getBeanManager().send(Constants.CALL_CONTROLLER_ACTION_COMMAND_NAME);
    }

    @Override
    public CompletableFuture<Void> destroy() {
        ControllerDestroyBean bean = context.getBeanManager().findAll(ControllerDestroyBean.class).get(0);
        bean.controlleridProperty().set(controllerId);
        return context.getBeanManager().send(Constants.DESTROY_CONTROLLER_COMMAND_NAME);
    }
}
