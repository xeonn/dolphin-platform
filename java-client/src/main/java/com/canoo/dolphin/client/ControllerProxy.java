package com.canoo.dolphin.client;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ControllerProxy<T> {

    T getModel();

    CompletableFuture<Void> invoke(String actionName);

    CompletableFuture<Void> destroy();
}
