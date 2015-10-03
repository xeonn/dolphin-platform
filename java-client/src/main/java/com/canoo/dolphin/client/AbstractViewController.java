package com.canoo.dolphin.client;

import javafx.beans.property.*;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractViewController<M> {

    private ControllerProxy<M> controllerProxy;

    private ReadOnlyBooleanWrapper actionCallRunning;

    private ReadOnlyObjectWrapper<M> model;

    private ReadOnlyObjectWrapper<Throwable> initializationException;

    private ReadOnlyObjectWrapper<Throwable> invocationException;

    public AbstractViewController(ClientContext clientContext, String controllerName) {
        model = new ReadOnlyObjectWrapper<>();
        actionCallRunning = new ReadOnlyBooleanWrapper(false);
        initializationException = new ReadOnlyObjectWrapper<>();
        invocationException = new ReadOnlyObjectWrapper<>();
        clientContext.<M>createController(controllerName).whenComplete((c, e) -> {
            if (e != null) {
                initializationException.set(e);
            } else {
                controllerProxy = c;
                model.set(c.getModel());
                init();
            }
        });
    }

    protected abstract void init();

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
                    invocationException.set(e);
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

    public Throwable getInvocationException() {
        return invocationException.get();
    }

    public ReadOnlyObjectProperty<Throwable> invocationExceptionProperty() {
        return invocationException.getReadOnlyProperty();
    }

    public Throwable getInitializationException() {
        return initializationException.get();
    }

    public ReadOnlyObjectProperty<Throwable> initializationExceptionProperty() {
        return initializationException.getReadOnlyProperty();
    }
}
