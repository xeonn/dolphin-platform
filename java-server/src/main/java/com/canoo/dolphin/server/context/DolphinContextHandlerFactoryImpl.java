package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.util.Assert;

import javax.servlet.ServletContext;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by hendrikebbers on 11.03.16.
 */
public class DolphinContextHandlerFactoryImpl implements DolphinContextHandlerFactory {

    @Override
    public DolphinContextHandler create(ServletContext servletContext) {
        Assert.requireNonNull(servletContext, "servletContext");
        ContainerManager containerManager = null;
        ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new RuntimeException("More than 1 " + ContainerManager.class + " found!");
            }
        } else {
            throw new RuntimeException("No " + ContainerManager.class + " found!");
        }
        containerManager.init(servletContext);

        ControllerRepository controllerRepository = new ControllerRepository();

        return new DolphinContextHandler(new DefaultOpenDolphinFactory(), containerManager, controllerRepository);
    }
}
