package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.server.event.impl.DolphinEventBusImpl;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.servlet.DolphinPlatformBoostrapException;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientIdFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientIdFilter.class);

    private static final String DOLPHIN_CONTEXT_MAP = "DOLPHIN_CONTEXT_MAP";

    private static final ThreadLocal<DolphinContext> currentContext = new ThreadLocal<>();

    private final DolphinPlatformConfiguration configuration;

    private List<DolphinSessionListener> contextListeners;

    private final ContainerManager containerManager;

    private final ControllerRepository controllerRepository;

    private final OpenDolphinFactory dolphinFactory;

    private final DolphinEventBusImpl dolphinEventBus;

    public ClientIdFilter(DolphinPlatformConfiguration configuration, ContainerManager containerManager, ControllerRepository controllerRepository, OpenDolphinFactory dolphinFactory, DolphinEventBusImpl dolphinEventBus) {
        this.configuration = configuration;
        this.containerManager = containerManager;
        this.controllerRepository = controllerRepository;
        this.dolphinFactory = dolphinFactory;
        this.dolphinEventBus = dolphinEventBus;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Nothing to do here
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        currentContext.set(null);
        try {
            final HttpServletRequest servletRequest = (HttpServletRequest) request;
            final HttpServletResponse servletResponse = (HttpServletResponse) response;
            final HttpSession httpSession = Assert.requireNonNull(servletRequest.getSession(), "request.getSession()");

            DolphinContext dolphinContext = null;
            final String clientId = servletRequest.getHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME);
            if (clientId == null || clientId.trim().isEmpty()) {
                if(getOrCreateClientMapInSession(httpSession).size() >= configuration.getMaxClientsPerSession()) {
                    servletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Maximum size for clients in session is reached");
                    LOG.error("Maximum size for clients in session " + servletRequest.getSession().getId() +" is reached", new DolphinContextException("Maximum size for clients in session is reached"));
                    return;
                }
                dolphinContext = createNewContext(httpSession);
                storeInSession(servletRequest.getSession(), dolphinContext);
                for(DolphinSessionListener listener : getAllListeners()) {
                    listener.sessionCreated(dolphinContext.getCurrentDolphinSession());
                }
                LOG.trace("Created new DolphinContext {} in http session {}", dolphinContext.getId(), httpSession.getId());
            } else {
                dolphinContext = getClientInSession(servletRequest.getSession(), clientId);
                if(dolphinContext == null) {
                    LOG.error("Can not find requested client for id " + clientId, new DolphinContextException("Can not find requested client!"));
                    servletResponse.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, "Can not find requested client!");
                    return;
                }
            }
            currentContext.set(dolphinContext);
            chain.doFilter(request, response);
            servletResponse.setHeader(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, dolphinContext.getId());
        } finally {
            currentContext.set(null);
        }
    }

    private DolphinContext createNewContext(final HttpSession httpSession) {
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
                LOG.trace("Destroying DolphinContext {} in http session {}", dolphinContext.getId(), httpSession.getId());
                Object contextList = httpSession.getAttribute(DOLPHIN_CONTEXT_MAP);
                if (contextList == null) {
                    return;
                }
                if (contextList instanceof List) {
                    ((List<DolphinContext>) contextList).remove(dolphinContext);
                }
            }
        };
        return new DolphinContext(containerManager, controllerRepository, dolphinFactory, dolphinEventBus, preDestroyCallback, onDestroyCallback);
    }

    public static void removeAllContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        List<DolphinContext> currentContexts = new ArrayList<>(getOrCreateClientMapInSession(session).values());
        for (DolphinContext context : currentContexts) {
            context.destroy();
        }
        session.removeAttribute(DOLPHIN_CONTEXT_MAP);
    }

    public static DolphinContext getCurrentContext() {
        return currentContext.get();
    }

    private DolphinContext getClientInSession(HttpSession session, String clientId) {
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if(contextMap == null) {
            return null;
        }
        return ((Map<String, DolphinContext>) contextMap).get(clientId);
    }

    private void storeInSession(HttpSession session, DolphinContext context) {
        getOrCreateClientMapInSession(session).put(context.getId(), context);
    }

    private static Map<String, DolphinContext> getOrCreateClientMapInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if (contextMap == null) {
            contextMap = new HashMap<>();
            session.setAttribute(DOLPHIN_CONTEXT_MAP, contextMap);
        }
        return (Map<String, DolphinContext>) contextMap;
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

    @Override
    public void destroy() {
    }
}
