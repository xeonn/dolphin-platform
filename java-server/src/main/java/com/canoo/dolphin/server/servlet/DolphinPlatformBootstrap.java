/**
 * Copyright 2015-2016 Canoo Engineering AG.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextCleaner;
import com.canoo.dolphin.server.context.DolphinContextHandler;
import com.canoo.dolphin.server.context.DolphinContextHandlerFactory;
import com.canoo.dolphin.server.context.DolphinContextHandlerFactoryImpl;
import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.server.adapter.InvalidationServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public class DolphinPlatformBootstrap {

    private static final DolphinPlatformBootstrap INSTANCE = new DolphinPlatformBootstrap();

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformBootstrap.class);

    public static final String DOLPHIN_SERVLET_NAME = "dolphin-platform-servlet";

    public static final String DOLPHIN_CROSS_SITE_FILTER_NAME = "dolphinCrossSiteFilter";

    public static final String DOLPHIN_INVALIDATION_SERVLET_NAME = "dolphin-platform-invalidation-servlet";

    public static final String DEFAULT_DOLPHIN_SERVLET_MAPPING = "/dolphin";

    public static final String DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING = "/dolphininvalidate";

    private String dolphinServletMapping;

    private String dolphinInvalidationServletMapping;

    private DolphinContextHandlerFactory dolphinContextHandlerFactory;

    private DolphinContextHandler dolphinContextHandler;

    private DolphinPlatformBootstrap() {}

    /**
     * This methods starts the Dolphin Platform server runtime
     * @param servletContext the servlet context
     */
    public void start(ServletContext servletContext) {
        dolphinContextHandlerFactory = new DolphinContextHandlerFactoryImpl();
        this.dolphinServletMapping = DEFAULT_DOLPHIN_SERVLET_MAPPING;
        this.dolphinInvalidationServletMapping = DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING;

        Assert.requireNonNull(servletContext, "servletContext");

        ControllerRepository controllerRepository = new ControllerRepository();
        ContainerManager containerManager = findManager();
        containerManager.init(servletContext);

        dolphinContextHandler = dolphinContextHandlerFactory.create(controllerRepository, containerManager);

        servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(dolphinContextHandler)).addMapping(dolphinServletMapping);
        servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, new InvalidationServlet()).addMapping(dolphinInvalidationServletMapping);
        servletContext.addFilter(DOLPHIN_CROSS_SITE_FILTER_NAME, new CrossSiteOriginFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        DolphinContextCleaner dolphinContextCleaner = new DolphinContextCleaner();
        dolphinContextCleaner.init(dolphinContextHandler);
        servletContext.addListener(dolphinContextCleaner);

        Set<Class<?>> listeners = ClasspathScanner.getInstance().getTypesAnnotatedWith(DolphinListener.class);
        for (Class<?> listenerClass : listeners) {
            if (DolphinBoostrapListener.class.isAssignableFrom(listenerClass)) {
                try {
                    DolphinBoostrapListener listener = (DolphinBoostrapListener) containerManager.createListener(listenerClass);
                    listener.dolphinRuntimeCreated();
                } catch (Exception e) {
                    LOG.error("Error in calling DolphinBoostrapListener " + listenerClass, e);
                    throw new DolphinPlatformBoostrapException("Error in calling DolphinBoostrapListener " + listenerClass, e);
                }
            }
        }

        LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Dolphin Platform endpoint defined as " + dolphinServletMapping);
    }

    public DolphinContextHandler getContextHandler() {
        return dolphinContextHandler;
    }

    public DolphinContext getCurrentContext() {
        return getContextHandler().getCurrentContext();
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
}
