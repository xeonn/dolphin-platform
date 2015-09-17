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
        if (controllerProxy != null) {
            return controllerProxy.destroy();
        }
        CompletableFuture<Void> dummy = new CompletableFuture<>();
        dummy.complete(null);
        return dummy;
    }
}
