package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.OpenDolphinFactory;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.DefaultServerDolphin;

/**
 * Created by hendrikebbers on 20.05.16.
 */
public class DolphinTestContext extends DolphinContext {

    private final DefaultInMemoryConfig config;

    private final DolphinEventBusImpl dolphinEventBus;

    public DolphinTestContext(ContainerManager containerManager, ControllerRepository controllerRepository, DefaultInMemoryConfig config, DolphinEventBusImpl dolphinEventBus) {
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

    private static OpenDolphinFactory createServerDolphinFactory(final DefaultInMemoryConfig config) {
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
