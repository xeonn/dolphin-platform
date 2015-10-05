package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.ControllerDestroyBean;
import org.opendolphin.StringUtil;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private final String controllerId;

    private final ClientContext context;

    private T model;

    private boolean destroyed = false;

    public ControllerProxyImpl(String controllerId, T model, ClientContext context) {
        if (StringUtil.isBlank(controllerId)) {
            throw new NullPointerException("controllerId must not be null");
        }
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        this.controllerId = controllerId;
        this.model = model;
        this.context = context;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public CompletableFuture<Void> invoke(String actionName, Param... params) {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        ControllerActionCallBean bean = context.getBeanManager().findAll(ControllerActionCallBean.class).get(0);
        bean.setControllerId(controllerId);
        bean.setActionName(actionName);
        return context.getBeanManager().invoke(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, params);
    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;
        ControllerDestroyBean bean = context.getBeanManager().findAll(ControllerDestroyBean.class).get(0);
        bean.setControllerId(controllerId);
        return context.getBeanManager().invoke(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME).thenAccept(v -> {
            model = null;
        });
    }
}
