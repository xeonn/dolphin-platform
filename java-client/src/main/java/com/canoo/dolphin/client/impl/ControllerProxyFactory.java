package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ControllerProxy;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 18.02.16.
 */
public interface ControllerProxyFactory {

    <T> CompletableFuture<ControllerProxy<T>> create(String name);
}
