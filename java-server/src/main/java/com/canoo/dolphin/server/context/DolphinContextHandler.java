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

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.comm.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DolphinContextHandler implements DolphinContextProvider {

    private static final Map<String, List<DolphinContext>> globalContextMap = new HashMap<>();

    private static final Lock globalContextMapLock = new ReentrantLock();

    private static final ThreadLocal<DolphinContext> currentContextThreadLocal = new ThreadLocal<>();

    private final ContainerManager containerManager;

    private final ControllerRepository controllerRepository;

    private final OpenDolphinFactory openDolphinFactory;

    private final DolphinEventBusImpl dolphinEventBus;

    public DolphinContextHandler(OpenDolphinFactory openDolphinFactory, ContainerManager containerManager, ControllerRepository controllerRepository) {
        this.openDolphinFactory = Assert.requireNonNull(openDolphinFactory, "openDolphinFactory");
        this.containerManager = Assert.requireNonNull(containerManager, "containerManager");
        this.controllerRepository = Assert.requireNonNull(controllerRepository, "controllerRepository");
        this.dolphinEventBus = new DolphinEventBusImpl(this);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");
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
                currentContext = new DolphinContext(containerManager, controllerRepository, openDolphinFactory, dolphinEventBus);
                contextList.add(currentContext);
            } else {
                currentContext = contextList.get(0);
            }
        } finally {
            globalContextMapLock.unlock();
        }
        currentContextThreadLocal.set(currentContext);
        try {
            response.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, currentContext.getId());
            response.setHeader("Content-Type", "application/json");
            response.setCharacterEncoding("UTF-8");

            //copied from DolphinServlet
            StringBuilder requestJson = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestJson.append(line).append("\n");
            }
            List<Command> commands = currentContext.getDolphin().getServerConnector().getCodec().decode(requestJson.toString());
            List<Command> results = currentContext.handle(commands);
            String jsonResponse = currentContext.getDolphin().getServerConnector().getCodec().encode(results);
            response.getWriter().print(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error in Dolphin command handling", e);
        } finally {
            currentContextThreadLocal.remove();
        }
    }

    public void removeAllContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        throw new RuntimeException("NYI");
    }

    public DolphinContext getCurrentContext() {
        return currentContextThreadLocal.get();
    }

    public Iterable<DolphinContext> getContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        return globalContextMap.get(session.getId());
    }

    public DolphinEventBusImpl getDolphinEventBus() {
        return dolphinEventBus;
    }
}
