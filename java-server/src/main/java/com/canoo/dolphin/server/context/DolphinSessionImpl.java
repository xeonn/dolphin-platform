package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DolphinSessionImpl implements DolphinSession {

    private Map<String, Object> store;

    private DolphinContext dolphinContext;

    public DolphinSessionImpl(DolphinContext dolphinContext) {
        this.dolphinContext = dolphinContext;
        this.store = Collections.synchronizedMap(new HashMap<String, Object>());
    }

    @Override
    public void setAttribute(String name, Object value) {
        store.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return store.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        store.remove(name);
    }

    @Override
    public void invalidate() {
        store.clear();
    }

    @Override
    public String getId() {
        return dolphinContext.getId();
    }
}
