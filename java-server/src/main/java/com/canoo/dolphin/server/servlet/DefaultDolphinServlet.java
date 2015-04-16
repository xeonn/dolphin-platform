package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultDolphinServlet extends DolphinServlet {

    private final DolphinCommandManager dolphinCommandRepository;

    private final Set<Class<?>> dolphinManagedClasses;

    private static ThreadLocal<ServletContext> servletContext = new ThreadLocal<>();

    private static ThreadLocal<ServerDolphin> dolphin = new ThreadLocal<>();

    public DefaultDolphinServlet() {
        ServiceLoader<DolphinCommandManager> serviceLoader = ServiceLoader.load(DolphinCommandManager.class);
        Iterator<DolphinCommandManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            this.dolphinCommandRepository = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new RuntimeException("More than 1 " + DolphinCommandManager.class + " found!");
            }
        } else {
            throw new RuntimeException("No " + DolphinCommandManager.class + " found!");
        }

        dolphinManagedClasses = DolphinPlatformBootstrap.findAllDolphinBeanClasses();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletContext.set(req.getServletContext());
        dolphin.set((ServerDolphin) req.getSession(true).getAttribute(DolphinServlet.class.getName()));
        super.service(req, resp);
        servletContext.remove();
        dolphin.remove();
    }

    public static ServerDolphin getServerDolphin() {
        ServerDolphin serverDolphin = dolphin.get();
        return serverDolphin;
    }

    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        dolphin.set(serverDolphin);
        ServletContext context = servletContext.get();
        dolphinCommandRepository.initCommandsForSession(context, serverDolphin, dolphinManagedClasses);
    }

}
