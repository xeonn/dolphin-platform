package com.canoo.dolphin.server.event;

import com.canoo.dolphin.mapping.ReferenceIdentifier;

/**
 * The dolphin event bus that can be used to send messages to other dolphin sessions.
 */
public interface DolphinEventBus {

    /**
     * Publish a message to the given address
     * @param address the address
     * @param message the message
     */
    void publish(String address, Object message);

    /**
     * Register as a handler / listener for a given address. All messages that will be published for the given address
     * by any dolphin session will trigger the given handler in the correct dolphin session.
     * @param address the address
     * @param handler the handler
     * @return a unique identifier that can be used to unregister the handler
     */
    ReferenceIdentifier registerHandler(String address, EventHandler handler);

    /**
     * Unregister the handler / listener. The needed {@link com.canoo.dolphin.mapping.ReferenceIdentifier} must be
     * created by calling {@link #registerHandler(String, EventHandler)}.
     * @param handlerIdentifier the identifier for the registered handler
     */
    void unregisterHandler(ReferenceIdentifier handlerIdentifier);
}
