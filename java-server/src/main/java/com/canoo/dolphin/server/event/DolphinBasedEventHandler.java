package com.canoo.dolphin.server.event;

import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinBasedEventHandler {

    private static DolphinBasedEventHandler instance = new DolphinBasedEventHandler();

    private Map<String, Map<EventHandler, Object>> unhandledHandlers;

    private DolphinBasedEventHandler() {
    }

    public void sendEventsForCurrentDolphinSession() {
        String dolphinId = DefaultDolphinServlet.getDolphinId();

        Map<EventHandler, Object> handlers = unhandledHandlers.remove(dolphinId);
        if(handlers != null && !handlers.isEmpty()) {
            for(Map.Entry<EventHandler, Object> handler : handlers.entrySet()) {
                handler.getKey().onEvent(handler.getValue());
            }
        }
    }

    public static DolphinBasedEventHandler getInstance() {
        return instance;
    }

    public void publish(String dolphinId, EventHandler handler, Object value) {
        Map<EventHandler, Object> map = unhandledHandlers.get(dolphinId);
        if(map == null) {
            map = new HashMap<>();
            unhandledHandlers.put(dolphinId, map);
        }
        map.put(handler, value);
    }

    public void unregisterHandlersForCurrentSession() {
        String dolphinId = DefaultDolphinServlet.getDolphinId();
        unhandledHandlers.remove(dolphinId);
    }
}
