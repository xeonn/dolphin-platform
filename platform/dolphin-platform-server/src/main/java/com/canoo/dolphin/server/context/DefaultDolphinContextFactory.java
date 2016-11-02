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
package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSessionListener;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public class DefaultDolphinContextFactory implements DolphinContextFactory {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DefaultDolphinContextFactory.class);

    private final DolphinPlatformConfiguration configuration;

    private final ControllerRepository controllerRepository;

    private final OpenDolphinFactory dolphinFactory;

    private final ContainerManager containerManager;

    private final DolphinEventBus dolphinEventBus;

    public DefaultDolphinContextFactory(final DolphinPlatformConfiguration configuration, final ContainerManager containerManager, final DolphinEventBus dolphinEventBus, final ClasspathScanner scanner) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.containerManager = Assert.requireNonNull(containerManager, "containerManager");
        this.dolphinEventBus = Assert.requireNonNull(dolphinEventBus, "dolphinEventBus");
        this.controllerRepository = new ControllerRepository(scanner);
        this.dolphinFactory = new DefaultOpenDolphinFactory();
    }

    @Override
    public DolphinContext create(final HttpSession httpSession, final DolphinSessionListenerProvider dolphinSessionListenerProvider) {
        Assert.requireNonNull(httpSession, "httpSession");
        Assert.requireNonNull(dolphinSessionListenerProvider, "dolphinSessionListenerProvider");

        final Callback<DolphinContext> preDestroyCallback = new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext dolphinContext) {
                Assert.requireNonNull(dolphinContext, "dolphinContext");
                for(DolphinSessionListener listener : dolphinSessionListenerProvider.getAllListeners()) {
                    listener.sessionDestroyed(dolphinContext.getDolphinSession());
                }
            }
        };

        final Callback<DolphinContext> onDestroyCallback = new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext dolphinContext) {
                Assert.requireNonNull(dolphinContext, "dolphinContext");
                LOG.trace("Destroying DolphinContext {} in http session {}", dolphinContext.getId(), httpSession.getId());
                DolphinContextUtils.removeFromSession(httpSession, dolphinContext);
            }
        };
        return new DolphinContext(configuration, containerManager, controllerRepository, dolphinFactory, preDestroyCallback, onDestroyCallback);
    }
}
