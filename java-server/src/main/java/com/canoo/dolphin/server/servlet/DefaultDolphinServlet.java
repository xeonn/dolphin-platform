package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultDolphinServlet extends DolphinServlet {

    private final DolphinCommandManager dolphinCommandRepository;

    private final Set<Class<?>> dolphinManagedClasses;

    private ServletContext servletContext;

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
        servletContext = req.getServletContext();
        super.service(req, resp);
    }


    @Override
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        dolphinCommandRepository.initCommandsForSession(servletContext, serverDolphin, dolphinManagedClasses);
    }

}
