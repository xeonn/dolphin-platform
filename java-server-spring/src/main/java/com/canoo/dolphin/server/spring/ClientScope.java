package com.canoo.dolphin.server.spring;


import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by hendrikebbers on 01.03.16.
 */
public class ClientScope implements Scope {

    private WeakHashMap<DolphinContext, Map<String, Object>> store;

    private DolphinContextProvider dolphinContextProvider;

    public ClientScope(DolphinContextProvider dolphinContextProvider) {
        this.store = new WeakHashMap<>();
        this.dolphinContextProvider = dolphinContextProvider;
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
        DolphinContext currentContext = getContext();
        Map<String, Object> localStore = store.get(currentContext);
        if(localStore == null) {
            localStore = Collections.synchronizedMap(new HashMap<String, Object>());
            store.put(currentContext, localStore);
        }
        return localStore;
    }

    @Override
    public String getConversationId() {
        return getContext().getId();
    }

    private DolphinContext getContext() {
        return dolphinContextProvider.getCurrentContext();
    }
}
