package com.canoo.dolphin.server.servlet;

import com.canoo.dolphin.server.container.DolphinCommandManager;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.server.adapter.DolphinServlet;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultDolphinServlet extends DolphinServlet {

    private final DolphinCommandManager dolphinCommandRepository;

    private final Set<Class<?>> dolphinManagedClasses;

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
    protected void registerApplicationActions(ServerDolphin serverDolphin) {
        dolphinCommandRepository.initCommandsForSession(serverDolphin, dolphinManagedClasses);
    }

}
