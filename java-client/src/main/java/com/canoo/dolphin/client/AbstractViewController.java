package com.canoo.dolphin.client;

import javafx.beans.property.*;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractViewController<M> {

    private ControllerProxy<M> controllerProxy;

    private ReadOnlyBooleanWrapper actionCallRunning;

    private ReadOnlyObjectWrapper<M> model;

    public AbstractViewController(ClientContext clientContext, String controllerName) {
        model = new ReadOnlyObjectWrapper<>();
        actionCallRunning = new ReadOnlyBooleanWrapper(false);
        clientContext.<M>createController(controllerName).whenComplete((c, e) -> {
            if (e != null) {
                onInitializationException(e);
            } else {
                controllerProxy = c;
                model.set(c.getModel());
                init();
            }
        });
    }

    protected abstract void init();

    protected void onInvocationException(Throwable t) {
        t.printStackTrace();
    }

    protected void onInitializationException(Throwable t) {
        t.printStackTrace();
    }

    public CompletableFuture<Void> destroy() {
        CompletableFuture<Void> ret;
        if (controllerProxy != null) {
            ret = controllerProxy.destroy();
            controllerProxy = null;
        } else {
            ret = new CompletableFuture<>();
            ret.complete(null);
        }
        return ret;
    }

    protected void invoke(String actionName, Param... params) {
        actionCallRunning.set(true);
        controllerProxy.invoke(actionName, params).whenComplete((v, e) -> {
            try {
                if (e != null) {
                    onInvocationException(e);
                }
            } finally {
                actionCallRunning.set(false);
            }
        });
    }

    public boolean isActionCallRunning() {
        return actionCallRunning.get();
    }

    public ReadOnlyBooleanProperty actionCallRunningProperty() {
        return actionCallRunning.getReadOnlyProperty();
    }

    public M getModel() {
        return model.get();
    }

    public ReadOnlyObjectProperty<M> modelProperty() {
        return model.getReadOnlyProperty();
    }
}
