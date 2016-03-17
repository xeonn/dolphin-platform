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
    public DolphinContextHandler create(ServletContext servletContext, ControllerRepository controllerRepository) {
        Assert.requireNonNull(servletContext, "servletContext");
        Assert.requireNonNull(controllerRepository, "controllerRepository");

        ContainerManager containerManager = findManager();
        containerManager.init(servletContext);
        return new DolphinContextHandler(new DefaultOpenDolphinFactory(), containerManager, controllerRepository);
    }

    private ContainerManager findManager() {
        ContainerManager containerManager = null;
        ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ContainerManager.class + " found!");
            }
        } else {
            throw new IllegalStateException("No " + ContainerManager.class + " found!");
        }
        return containerManager;
    }
}
