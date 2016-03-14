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
import org.opendolphin.core.comm.Command;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Class that manages all dolphin contexts (see {@link DolphinContext}).
 * This class will be refactored in a furure version.
 */
public class DolphinContextHandler {

    private static final String DOLPHIN_CONTEXT_MAP = "DOLPHIN_CONTEXT_MAP";

    private static final ThreadLocal<DolphinContext> currentContextThreadLocal = new ThreadLocal<>();

    private final ContainerManager containerManager;

    private final static DolphinContextHandler INSTANCE = new DolphinContextHandler();

    private OpenDolphinFactory openDolphinFactory;

    private ControllerRepository controllerRepository;

    private DolphinContextHandler() {
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
        openDolphinFactory = new DefaultOpenDolphinFactory();

        controllerRepository = new ControllerRepository();

    }

    public void init(ServletContext servletContext) {
        this.containerManager.init(servletContext);
    }

    public void handle(final HttpServletRequest request, final HttpServletResponse response) {
        final HttpSession httpSession = request.getSession();
        //This will refactored later to support a client scope (tab based in browser)
        currentContextThreadLocal.remove();
        DolphinContext currentContext;

        if(getContexts(request.getSession()).isEmpty()) {
            Callback<DolphinContext> onDestroyCallback = new Callback<DolphinContext>() {
                @Override
                public void call(DolphinContext dolphinContext) {
                    Object contextList = httpSession.getAttribute(DOLPHIN_CONTEXT_MAP);
                    if (contextList == null) {
                        return;
                    }
                    if(contextList instanceof List) {
                        ((List<DolphinContext>) contextList).remove(dolphinContext);
                    }
                }
            };
            currentContext = new DolphinContext(containerManager, controllerRepository, openDolphinFactory, onDestroyCallback);
            ArrayList list = new ArrayList();
            list.add(currentContext);
            request.getSession().setAttribute(DOLPHIN_CONTEXT_MAP, list);
        } else {
            //TODO: Curtently there is only 1 dolphin context in each session
            currentContext = getContexts(request.getSession()).get(0);
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

    /**
     * Deprecated because of the client scope that will iontroduced in the next version
     *
     * @param session
     * @return
     */
    public static List<DolphinContext> getContexts(HttpSession session) {
        Object contextList = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if (contextList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List) contextList);
    }

    public static DolphinContext getCurrentContext() {
        return currentContextThreadLocal.get();
    }

    public static void removeAllContextsInSession(HttpSession session) {
        for (DolphinContext context : getContexts(session)) {
            context.destroy();
        }
        session.removeAttribute(DOLPHIN_CONTEXT_MAP);
    }

    public static DolphinContextHandler getInstance() {
        return INSTANCE;
    }
}
