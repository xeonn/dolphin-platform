package com.canoo.dolphin.client;

import java.util.concurrent.CompletableFuture;

public interface ControllerProxy<T> {

    T getModel();

    CompletableFuture<Void> invoke(String actionName, Param... params);

    CompletableFuture<Void> destroy();
}
