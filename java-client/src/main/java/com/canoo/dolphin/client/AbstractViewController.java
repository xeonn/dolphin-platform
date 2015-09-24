package com.canoo.dolphin.client;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractViewController<M> {

    private ControllerProxy<M> controllerProxy;

    public AbstractViewController(ClientContext clientContext, String controllerName) {
        clientContext.<M>createController(controllerName).thenAccept(this::init);
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
