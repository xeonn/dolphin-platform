package com.canoo.dolphin.client;

import java.util.concurrent.CompletableFuture;

public interface ClientContext {

    <T> CompletableFuture<ControllerProxy<T>> createController(String name);

    ClientBeanManager getBeanManager();

    CompletableFuture<Void> disconnect();
}
