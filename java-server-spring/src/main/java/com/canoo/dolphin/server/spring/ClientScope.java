package com.canoo.dolphin.server.spring;


import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public class ClientScope implements Scope {

    public final static String CLIENT_SCOPE = "client";

    private final static String CLIENT_STORE_ATTRIBUTE = "DolphinPlatformSpringClientScopeStore";

    private DolphinSessionProvider dolphinSessionProvider;

    public ClientScope(DolphinSessionProvider dolphinSessionProvider) {
        this.dolphinSessionProvider = dolphinSessionProvider;
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> localStore = getLocalStore();
        if (!localStore.containsKey(name)) {
            localStore.put(name, objectFactory.getObject());
        }
        return localStore.get(name);
    }

    @Override
    public Object remove(String name) {
        return getLocalStore().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    private Map<String, Object> getLocalStore() {
        Map<String, Object> localStore = getDolphinSession().getAttribute(CLIENT_STORE_ATTRIBUTE);
        if(localStore == null) {
            localStore = Collections.synchronizedMap(new HashMap<String, Object>());
            getDolphinSession().setAttribute(CLIENT_STORE_ATTRIBUTE, localStore);
        }
        return localStore;
    }

    @Override
    public String getConversationId() {
        return getDolphinSession().getId();
    }

    private DolphinSession getDolphinSession() {
        return dolphinSessionProvider.getDolphinSession();
    }
}
