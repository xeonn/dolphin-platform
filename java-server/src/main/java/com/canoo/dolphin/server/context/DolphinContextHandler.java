package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DolphinContextHandler {

    private final static Map<String, List<DolphinContext>> globalContextMap = new HashMap<>();

    private final static Lock globalContextMapLock = new ReentrantLock();

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
        globalContextMapLock.lock();
        try {
            List<DolphinContext> contextList = globalContextMap.get(request.getSession().getId());
            if (contextList == null) {
                contextList = new ArrayList<>();
                globalContextMap.put(request.getSession().getId(), contextList);
            }
            if (contextList.isEmpty()) {
                currentContext = new DolphinContext(containerManager, servletContext);
                contextList.add(currentContext);
            } else {
                currentContext = contextList.get(0);
            }
        } finally {
            globalContextMapLock.unlock();
        }
        currentContextThreadLocal.set(currentContext);
        return currentContext;
    }

    /**
     * Deprecated because of the client scope that will iontroduced in the next version
     * @param session
     * @return
     */
    @Deprecated
    public static DolphinContext getContext(HttpSession session) {
        globalContextMapLock.lock();
        try {
            List<DolphinContext> contextList = globalContextMap.get(session.getId());
            if (contextList != null && !contextList.isEmpty()) {
                return contextList.get(0);
            }
            return null;
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static DolphinContext getCurrentContext() {
        return currentContextThreadLocal.get();
    }

    public void resetCurrentContextThreadLocal() {
        currentContextThreadLocal.remove();
    }

    public static List<DolphinContext> getAllContexts() {
        globalContextMapLock.lock();
        try {
            List<DolphinContext> ret = new ArrayList<>();
            for (List<DolphinContext> sessionList : globalContextMap.values()) {
                ret.addAll(sessionList);
            }
            return ret;
        } finally {
            globalContextMapLock.unlock();
        }
    }

    public static List<DolphinContext> getAllContextsInSession() {
        return Arrays.asList(getCurrentContext());
    }
}
