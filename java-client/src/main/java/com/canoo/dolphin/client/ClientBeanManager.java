package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;

import java.util.concurrent.CompletableFuture;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ClientBeanManager extends BeanManager {

    CompletableFuture<Void> send(String command, Param... params);
}
