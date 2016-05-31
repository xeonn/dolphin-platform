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

import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.util.DolphinRemotingException;
import mockit.Mocked;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.testng.Assert.assertNull;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class DolphinContextHandlerTest {

    @Test
    public void testNoSession(@Mocked final ContainerManager containerManager) {
        //given:
        ControllerRepository controllerRepository = new ControllerRepository();
        OpenDolphinFactory openDolphinFactory = new DefaultOpenDolphinFactory();
        DolphinContextHandler contextHandler = new DolphinContextHandler(new DolphinPlatformConfiguration());

        //then:
        assertNull(contextHandler.getCurrentContext());
        assertNull(contextHandler.getCurrentDolphinSession());
    }

    @Test(expectedExceptions = DolphinRemotingException.class)
    public void testWrongProtocol(@Mocked final ContainerManager containerManager, @Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response) {
        //given:
        ControllerRepository controllerRepository = new ControllerRepository();
        OpenDolphinFactory openDolphinFactory = new DefaultOpenDolphinFactory();
        DolphinContextHandler contextHandler = new DolphinContextHandler(new DolphinPlatformConfiguration());

        //then:
        contextHandler.handle(request, response);
    }

}
