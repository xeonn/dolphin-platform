package com.canoo.dolphin.server.event;

import com.canoo.dolphin.server.servlet.DefaultDolphinServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class DolphinEventBusImpl implements DolphinEventBus {

    private Map<HandlerIdentifier, String> dolphinIdByIdentifier;

    private Map<HandlerIdentifier, EventHandler> handlersByIdentifier;

    private Map<HandlerIdentifier, String> addressByIdentifier;

    private Map<String, Map<String, List<EventHandler>>> registeredHandlers;

    public void publish(String address, Object value) {
        Map<String, List<EventHandler>> handlersForAddress = registeredHandlers.get(address);

        for(Map.Entry<String, List<EventHandler>> entry : handlersForAddress.entrySet()) {
            String dolphinId = entry.getKey();
            List<EventHandler> handlers = entry.getValue();

            for(EventHandler handler : handlers) {
                DolphinBasedEventHandler.getInstance().publish(dolphinId, handler, value);
            }
        }
    }

    public HandlerIdentifier registerHandler(String address, EventHandler handler) {
        Map<String, List<EventHandler>> handlersByDolphinId = registeredHandlers.get(address);
        if(handlersByDolphinId == null) {
            handlersByDolphinId = new HashMap<>();
            registeredHandlers.put(address, handlersByDolphinId);
        }

        String dolphinId = DefaultDolphinServlet.getDolphinId();
        List<EventHandler> list = handlersByDolphinId.get(dolphinId);
        if(list == null) {
            list = new ArrayList<>();
            handlersByDolphinId.put(dolphinId, list);
        }
        list.add(handler);

        HandlerIdentifier identifier = new HandlerIdentifier();
        handlersByIdentifier.put(identifier, handler);
        addressByIdentifier.put(identifier, address);
        dolphinIdByIdentifier.put(identifier, dolphinId);

        return identifier;
    }

    public void unregisterHandler(HandlerIdentifier handlerIdentifier) {
        EventHandler handler = handlersByIdentifier.remove(handlerIdentifier);
        String dolphinId = dolphinIdByIdentifier.remove(handlerIdentifier);
        String address = addressByIdentifier.remove(handlerIdentifier);

        registeredHandlers.get(address).get(dolphinId).remove(handler);
    }

    public void unregisterHandlersForCurrentSession() {
        String dolphinId = DefaultDolphinServlet.getDolphinId();

        for(Map.Entry<String, Map<String, List<EventHandler>>> entry : registeredHandlers.entrySet()) {
            List<EventHandler> removedHandlers = entry.getValue().remove(dolphinId);
            if(removedHandlers != null && !removedHandlers.isEmpty()) {
                //TODO: dolphinIdByIdentifier etc. noch clearen
            }
        }

        DolphinBasedEventHandler.getInstance().unregisterHandlersForCurrentSession();
    }
}
