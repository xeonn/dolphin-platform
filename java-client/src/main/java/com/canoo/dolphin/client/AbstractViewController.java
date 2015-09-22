package com.canoo.dolphin.client;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 16.09.15.
 */
public abstract class AbstractViewController<M> {

    private ControllerProxy<M> controllerProxy;

    public AbstractViewController(ClientContext clientContext, String controllerName) {
        clientContext.createController(controllerName).thenAccept(c -> {
            controllerProxy = controllerProxy;
            init((ControllerProxy<M>) c);
        });
    }

    protected abstract void init(ControllerProxy<M> controller);

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
}
