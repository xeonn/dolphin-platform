package com.canoo.dolphin.server.context;

import com.canoo.dolphin.util.Assert;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public class DolphinContextUtils {

    private static final String DOLPHIN_CONTEXT_MAP = "DOLPHIN_CONTEXT_MAP";

    private static final ThreadLocal<DolphinContext> currentContext = new ThreadLocal<>();

    public static DolphinContext getClientInSession(HttpSession session, String clientId) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonBlank(clientId, "clientId");
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if(contextMap == null) {
            return null;
        }
        return ((Map<String, DolphinContext>) contextMap).get(clientId);
    }

    public static void storeInSession(HttpSession session, DolphinContext context) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonNull(context, "context");
        getOrCreateContextMapInSession(session).put(context.getId(), context);
    }

    public static void removeFromSession(HttpSession session, DolphinContext context) {
        Assert.requireNonNull(session, "session");
        Assert.requireNonNull(context, "context");
        getOrCreateContextMapInSession(session).remove(context.getId());
    }

    public static Map<String, DolphinContext> getOrCreateContextMapInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        Object contextMap = session.getAttribute(DOLPHIN_CONTEXT_MAP);
        if (contextMap == null) {
            contextMap = new HashMap<>();
            session.setAttribute(DOLPHIN_CONTEXT_MAP, contextMap);
        }
        return (Map<String, DolphinContext>) contextMap;
    }

    public static void removeAllContextsInSession(HttpSession session) {
        Assert.requireNonNull(session, "session");
        List<DolphinContext> currentContexts = new ArrayList<>(getOrCreateContextMapInSession(session).values());
        for (DolphinContext context : currentContexts) {
            context.destroy();
        }
        session.removeAttribute(DOLPHIN_CONTEXT_MAP);
    }

    public static DolphinContext getContextForCurrentThread() {
        return currentContext.get();
    }

    public static void setContextForCurrentThread(DolphinContext dolphinContext) {
        currentContext.set(dolphinContext);
    }
}
