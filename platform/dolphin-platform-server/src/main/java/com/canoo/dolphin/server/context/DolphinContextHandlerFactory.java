/**
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

import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;

import javax.servlet.ServletContext;

/**
 * Factory that creates a {@link DolphinContextHandler}
 */
public interface DolphinContextHandlerFactory {

    /**
     * Returns a new {@link DolphinContextHandler} instance
     * @param servletContext the servletContext
     * @param controllerRepository the controllerRepository
     * @return the created instance
     */
    DolphinContextHandler create(DolphinPlatformConfiguration configuration, ControllerRepository controllerRepository, ContainerManager containerManager);

}
