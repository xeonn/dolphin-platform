/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.OpenDolphinFactory;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.server.DefaultServerDolphin;

public class DolphinTestContext extends DolphinContext {

    private final TestInMemoryConfiguration config;

    private final DolphinEventBusImpl dolphinEventBus;

    public DolphinTestContext(ContainerManager containerManager, ControllerRepository controllerRepository, TestInMemoryConfiguration config, DolphinEventBusImpl dolphinEventBus) {
        super(containerManager, controllerRepository, createServerDolphinFactory(config), dolphinEventBus, createEmptyCallback(), createEmptyCallback());
        this.config = Assert.requireNonNull(config, "config");
        this.dolphinEventBus = Assert.requireNonNull(dolphinEventBus, "dolphinEventBus");
    }

    private static Callback<DolphinContext> createEmptyCallback() {
        return new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext context) {

            }
        };
    }

    private static OpenDolphinFactory createServerDolphinFactory(final TestInMemoryConfiguration config) {
        Assert.requireNonNull(config, "config");
        return new OpenDolphinFactory(){

            @Override
            public DefaultServerDolphin create() {
                DefaultServerDolphin defaultServerDolphin =  config.getServerDolphin();
                return defaultServerDolphin;
            }
        };
    }

    public DolphinEventBusImpl getDolphinEventBus() {
        return dolphinEventBus;
    }

    public ClientDolphin getClientDolphin() {
        return config.getClientDolphin();
    }
}
