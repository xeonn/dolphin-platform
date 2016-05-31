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
package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.ClientIdFilter;
import com.canoo.dolphin.server.context.DefaultOpenDolphinFactory;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import com.canoo.dolphin.server.context.DolphinHttpSessionListener;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.server.adapter.InvalidationServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * This class defines the bootstrap for Dolphin Platform.
 */
public class DolphinPlatformBootstrap {

    private static final DolphinPlatformBootstrap INSTANCE = new DolphinPlatformBootstrap();

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DolphinPlatformBootstrap.class);

    public static final String DOLPHIN_SERVLET_NAME = "dolphin-platform-servlet";

    public static final String DOLPHIN_CROSS_SITE_FILTER_NAME = "dolphinCrossSiteFilter";

    public static final String DOLPHIN_INVALIDATION_SERVLET_NAME = "dolphin-platform-invalidation-servlet";

    public static final String DOLPHIN_CLIENT_ID_FILTER_NAME = "dolphin-platform-client-id-filter";

    public static final String DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING = "/dolphininvalidate";

    private DolphinContextHandler dolphinContextHandler;

    private DolphinEventBusImpl dolphinEventBus;

    private DolphinPlatformBootstrap() {
        dolphinEventBus = new DolphinEventBusImpl(new DolphinContextProvider() {
            @Override
            public DolphinContext getCurrentContext() {
                if(dolphinContextHandler == null) {
                    throw new DolphinPlatformBoostrapException("Dolphin Platform Event Bus can not be used before Dolphin Platform was initialized!");
                }
                DolphinContext context = dolphinContextHandler.getCurrentContext();
                if(context == null) {
                    throw new DolphinAccessException("This method can not be called outside of a Dolphin Platform request!");
                }
                return context;
            }

            @Override
            public DolphinSession getCurrentDolphinSession() {
                return getCurrentContext().getCurrentDolphinSession();
            }
        });
    }

    /**
     * This methods starts the Dolphin Platform server runtime
     * @param servletContext the servlet context
     */
    public void start(ServletContext servletContext, DolphinPlatformConfiguration configuration) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(configuration, "configuration");

        LOG.debug("Dolphin Platform starts with value for useCrossSiteOriginFilter=" + configuration.isUseCrossSiteOriginFilter());
        LOG.debug("Dolphin Platform starts with value for dolphinPlatformServletMapping=" + configuration.getDolphinPlatformServletMapping());
        LOG.debug("Dolphin Platform starts with value for openDolphinLogLevel=" + configuration.getOpenDolphinLogLevel());

        ControllerRepository controllerRepository = new ControllerRepository();
        ContainerManager containerManager = findManager();
        containerManager.init(servletContext);

        dolphinContextHandler = new DolphinContextHandler(configuration);


        servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(dolphinContextHandler)).addMapping(configuration.getDolphinPlatformServletMapping());
        servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, new InvalidationServlet()).addMapping(DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING);

        servletContext.addFilter(DOLPHIN_CLIENT_ID_FILTER_NAME, new ClientIdFilter(configuration, containerManager, controllerRepository, new DefaultOpenDolphinFactory(), dolphinEventBus)).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


        LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Dolphin Platform endpoint defined as " + configuration.getDolphinPlatformServletMapping());

        if (configuration.isUseCrossSiteOriginFilter()) {
            servletContext.addFilter(DOLPHIN_CROSS_SITE_FILTER_NAME, new CrossSiteOriginFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }

        DolphinHttpSessionListener contextCleaner = new DolphinHttpSessionListener(dolphinContextHandler, configuration);
        servletContext.addListener(contextCleaner);

        java.util.logging.Logger openDolphinLogger = Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(configuration.getOpenDolphinLogLevel());
    }

    public DolphinContextHandler getContextHandler() {
        return dolphinContextHandler;
    }

    public DolphinContext getCurrentContext() {
        return getContextHandler().getCurrentContext();
    }

    public DolphinSession getCurrentDolphinSession() {
        return getCurrentContext().getCurrentDolphinSession();
    }

    private ContainerManager findManager() {
        final ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        final Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            final ContainerManager containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ContainerManager.class + " found!");
            }
            return containerManager;
        } else {
            throw new IllegalStateException("No " + ContainerManager.class + " found!");
        }
    }

    public static DolphinPlatformBootstrap getInstance() {
        return INSTANCE;
    }

    public DolphinEventBusImpl getDolphinEventBus() {
        return dolphinEventBus;
    }
}
