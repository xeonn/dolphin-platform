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

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.servlet.DolphinPlatformBoostrapException;
import com.canoo.dolphin.server.servlet.DolphinPlatformBootstrap;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import com.canoo.dolphin.util.DolphinRemotingException;
import org.opendolphin.core.comm.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class manages all {@link DolphinContext} instances
 */
public class DolphinContextHandler implements DolphinContextProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinContextHandler.class);

    private static final String DOLPHIN_CONTEXT_MAP = "DOLPHIN_CONTEXT_MAP";

    private static final ThreadLocal<DolphinContext> currentContextThreadLocal = new ThreadLocal<>();

    private final ContainerManager containerManager;

    private final ControllerRepository controllerRepository;

    private final OpenDolphinFactory openDolphinFactory;

    private List<DolphinSessionListener> contextListeners;

    public DolphinContextHandler(OpenDolphinFactory openDolphinFactory, ContainerManager containerManager, ControllerRepository controllerRepository) {
        this.openDolphinFactory = Assert.requireNonNull(openDolphinFactory, "openDolphinFactory");
        this.containerManager = Assert.requireNonNull(containerManager, "containerManager");
        this.controllerRepository = Assert.requireNonNull(controllerRepository, "controllerRepository");
    }

    public void handle(final HttpServletRequest request, final HttpServletResponse response) {
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(response, "response");
        final HttpSession httpSession = Assert.requireNonNull(request.getSession(), "request.getSession()");

        DolphinContext currentContext = null;
        try {
            List<DolphinContext> contextsForSession = getContexts(httpSession);
            if (contextsForSession.isEmpty()) {

                final Callback<DolphinContext> preDestroyCallback = new Callback<DolphinContext>() {
                    @Override
                    public void call(DolphinContext dolphinContext) {
                        Assert.requireNonNull(dolphinContext, "dolphinContext");
                        for(DolphinSessionListener listener : getAllListeners()) {
                            listener.sessionDestroyed(dolphinContext.getCurrentDolphinSession());
                        }
                    }
                };

                final Callback<DolphinContext> onDestroyCallback = new Callback<DolphinContext>() {
                    @Override
                    public void call(DolphinContext dolphinContext) {
                        Assert.requireNonNull(dolphinContext, "dolphinContext");
                        LOG.info("Destroying DolphinContext " + dolphinContext.getId() + " in http session " + httpSession.getId());
                        Object contextList = httpSession.getAttribute(DOLPHIN_CONTEXT_MAP);
                        if (contextList == null) {
                            return;
                        }
                        if (contextList instanceof List) {
                            ((List<DolphinContext>) contextList).remove(dolphinContext);
                        }
                    }
                };


                currentContext = new DolphinContext(containerManager, controllerRepository, openDolphinFactory, DolphinPlatformBootstrap.getInstance().getDolphinEventBus(), preDestroyCallback, onDestroyCallback);
                ArrayList list = new ArrayList();
                list.add(currentContext);
                httpSession.setAttribute(DOLPHIN_CONTEXT_MAP, list);

                for(DolphinSessionListener listener : getAllListeners()) {
                    listener.sessionCreated(currentContext.getCurrentDolphinSession());
                }

                LOG.info("Created new DolphinContext " + currentContext.getId() + " in http session " + httpSession.getId());
            } else {
                //TODO: Curtently there is only 1 dolphin context in each session
                currentContext = getContexts(httpSession).get(0);
            }
        } catch (Exception e) {
            throw new DolphinContextException("Can not find or create matching dolphin context", e);
        }

        LOG.debug("Handling request for DolphinContext " + currentContext.getId() + " in http session " + httpSession.getId());

        currentContextThreadLocal.set(currentContext);
        try {
            final List<Command> commands = new ArrayList<>();
            try {
                StringBuilder requestJson = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    requestJson.append(line).append("\n");
                }
                commands.addAll(currentContext.getDolphin().getServerConnector().getCodec().decode(requestJson.toString()));
            } catch (Exception e) {
                throw new DolphinRemotingException("Can not parse request!", e);
            }

            final List<Command> results = new ArrayList<>();
            try {
                results.addAll(currentContext.handle(commands));
            } catch (Exception e) {
                throw new DolphinCommandException("Can not handle the commands", e);
            }

            try {
                response.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, currentContext.getId());
                response.setHeader("Content-Type", "application/json");
                response.setCharacterEncoding("UTF-8");

                final String jsonResponse = currentContext.getDolphin().getServerConnector().getCodec().encode(results);
                response.getWriter().print(jsonResponse);
            } catch (Exception e) {
                throw new DolphinRemotingException("Can not write response!", e);
            }
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
        Assert.requireNonNull(session, "session");
        Object contextList = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if (contextList == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List) contextList);
    }

    public DolphinContext getCurrentContext() {
        // TODO: Maybe it's better to throw an exception if no context:
        // throw new DolphinAccessException("This method can not be called outside of a Dolphin Platform request!");
        return currentContextThreadLocal.get();
    }

    /**
     * Returns true if the method is called in a thread that currently handles a Dolphin Platform request
     * @return true if the method is called in a thread that currently handles a Dolphin Platform request
     */
    public boolean isDolphinRequestThread() {
        return currentContextThreadLocal.get() != null;
    }

    public void removeAllContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        for (DolphinContext context : getContexts(session)) {
            context.destroy();
        }
        session.removeAttribute(DOLPHIN_CONTEXT_MAP);
    }

    @Override
    public DolphinSession getCurrentDolphinSession() {
        DolphinContext context = getCurrentContext();
        if (context == null) {
            return null;
        }
        return context.getCurrentDolphinSession();
    }

    private synchronized List<DolphinSessionListener> getAllListeners() {
        if(contextListeners == null) {
            contextListeners = new ArrayList<>();
            Set<Class<?>> listeners = ClasspathScanner.getInstance().getTypesAnnotatedWith(DolphinListener.class);
            for (Class<?> listenerClass : listeners) {
                try {
                    if (DolphinSessionListener.class.isAssignableFrom(listenerClass)) {
                        DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                        contextListeners.add(listener);
                    }
                } catch (Exception e) {
                    throw new DolphinPlatformBoostrapException("Error in creating DolphinSessionListener " + listenerClass, e);
                }
            }
        }
        return contextListeners;
    }
}
