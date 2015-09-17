package com.canoo.dolphin.client;

import org.opendolphin.core.client.ClientDolphin;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ClientContext {

    <T> CompletableFuture<ControllerProxy<T>> createController(String name);

    ClientBeanManager getBeanManager();

    void disconnect();

    ClientDolphin getDolphin();

}
