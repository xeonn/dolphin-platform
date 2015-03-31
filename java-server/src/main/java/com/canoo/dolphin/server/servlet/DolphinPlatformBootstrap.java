package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.DolphinController;
import org.opendolphin.server.adapter.InvalidationServlet;
import org.reflections.Reflections;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class DolphinPlatformBootstrap implements ServletContainerInitializer {

    public static final String DOLPHIN_SERVLET_NAME = "dolphin-platform-servlet";

    public static final String DOLPHIN_INVALIDATION_SERVLET_NAME = "dolphin-platform-invalidation-servlet";

    public static final String DEFAULT_DOLPHIN_SERVLET_MAPPING = "/dolphin";

    public static final String DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING = "/dolphininvalidate";

    private String dolphinServletMapping;

    private String dolphinInvalidationServletMapping;

    public DolphinPlatformBootstrap() {
        this.dolphinServletMapping = DEFAULT_DOLPHIN_SERVLET_MAPPING;
        this.dolphinInvalidationServletMapping = DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING;
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        servletContext.addServlet(DOLPHIN_SERVLET_NAME, DefaultDolphinServlet.class).addMapping(dolphinServletMapping);
        servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, InvalidationServlet.class).addMapping(dolphinInvalidationServletMapping);
    }

    private static Set<Class<?>> cachedDolphinBeanClasses;

    public static synchronized Set<Class<?>> findAllDolphinBeanClasses() {
        if(cachedDolphinBeanClasses == null) {
            Reflections reflections = new Reflections("");
            cachedDolphinBeanClasses = reflections.getTypesAnnotatedWith(DolphinController.class);
        }
        return cachedDolphinBeanClasses;
    }
}
