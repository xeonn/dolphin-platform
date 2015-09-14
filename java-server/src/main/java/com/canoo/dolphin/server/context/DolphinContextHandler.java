package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.ServiceLoader;

public class DolphinContextHandler {

    private final static String DOLPHIN_SESSION_ATTRIBUTE_NAME = "DolphinContext";

    private final static ThreadLocal<DolphinContext> currentContextThreadLocal = new ThreadLocal<>();

    private final ContainerManager containerManager;

    private ServletContext servletContext;

    public DolphinContextHandler(ServletContext servletContext) {
        this.servletContext = servletContext;
        ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            this.containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new RuntimeException("More than 1 " + ContainerManager.class + " found!");
            }
        } else {
            throw new RuntimeException("No " + ContainerManager.class + " found!");
        }

        ControllerHandler.init();
    }

    public DolphinContext getCurrentDolphinContext(HttpServletRequest request, HttpServletResponse response) {
        //This will refactored later to support a client scope (tab based in browser)

        currentContextThreadLocal.remove();

        DolphinContext currentContext;

        Object context = request.getSession().getAttribute(DOLPHIN_SESSION_ATTRIBUTE_NAME);
        if (context == null) {
            DolphinContext dolphinContext = new DolphinContext(containerManager, servletContext);
            request.getSession().setAttribute(DOLPHIN_SESSION_ATTRIBUTE_NAME, dolphinContext);
            currentContext = dolphinContext;
        } else if (context instanceof DolphinContext) {
            currentContext = (DolphinContext) context;
        } else {
            throw new RuntimeException("ERROR");
        }
        currentContextThreadLocal.set(currentContext);
        return currentContext;
    }

    @Deprecated
    public static DolphinContext getContext(HttpSession session) {
        Object context = session.getAttribute(DOLPHIN_SESSION_ATTRIBUTE_NAME);
        if (context instanceof DolphinContext) {
            return (DolphinContext) context;
        }
        return null;
    }

    public static DolphinContext getCurrentContext() {
        return currentContextThreadLocal.get();
    }

    public void resetCurrentContextThreadLocal() {
        currentContextThreadLocal.remove();
    }
}
