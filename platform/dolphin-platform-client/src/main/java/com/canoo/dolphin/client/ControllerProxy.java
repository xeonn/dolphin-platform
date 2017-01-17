/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.client;

import java8.util.concurrent.CompletableFuture;

/**
 * A proxy that can be used to interact with the server side controller. Whenever a {@link ControllerProxy} is created
 * in the client a matching controller instance is created on the server side. The proxy can be used to interact with
 * the controller instance on the server side and to work with the model of the MVC group.
 * <center><img src="doc-files/proxy.png" alt="client proxy"></center>
 * It's important that a controller proxy will be destroyed when the view isn't needed anymore. When calling the {@link #destroy()}
 * method on teh client the server side controller will be destroyed, too. Otherwiese the controller on the server will
 * automatically be destroyed when the client will be exited or the {@link ClientContext} will be disconnected (see {@link ClientContext#disconnect()}).
 */
public interface ControllerProxy<T> {

    /**
     * Returns the model aof the defined MVC group. This model will automatically be synchronized with the server. If no
     * model is specified in the server controller (see {@literal @}DolphinModel annotation for more details) this method will return null.
     * @return the model or null
     */
    T getModel();

    /**
     * By calling this method an action will be called on the server side controller (see {@literal @}DolphinAction
     * annotation for more details). The method won't block until the action was handled on the server. If you need this
     * information the method returns a {@link CompletableFuture} that is completed when the action was on the server.
     * @param actionName unique name of the action (defined by method name or {@literal @}DolphinAction annotation in the
     *                   server controller.
     * @param params params that can be used for the action call. The server action must define the sama param count with
     *               matching names.
     * @return A future that is completed when the action was handled on the server. If an exception was thrown on the
     * server side action the {@link CompletableFuture#get()} call will throw an exception.
     */
    CompletableFuture<Void> invoke(String actionName, Param... params);

    /**
     * Destroys this proxy and the controller on the server side.
     * @return A future that is completed when the destruction was handled on the server. If an exception was thrown on the
     * server side the {@link CompletableFuture#get()} call will throw an exception.
     */
    CompletableFuture<Void> destroy();

    /**
     * Returns the unique ID for the controller.
     * @return the unique ID for the controller
     */
    String getId();

    /**
     * Creates a {@link ControllerProxy} instance for the controller with the given name.
     * Currently this methods does exactly the same as {@link ClientContext#createController(String)} but in a future
     * release controller hierarchies will be supported by this method.
     * @param name the unique name of the controller type
     * @param <T> the type of the model that is bound to the controller and view
     * @return a {@link CompletableFuture} that defines the creation of the controller.
     */
    <T> CompletableFuture<ControllerProxy<T>> createController(String name);
}
