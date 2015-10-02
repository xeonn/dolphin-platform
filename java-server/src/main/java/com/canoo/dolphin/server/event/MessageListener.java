package com.canoo.dolphin.server.event;

/**
 * A handler that can be registered to the dolphin message bus (see {@link com.canoo.dolphin.server.event.DolphinEventBus})
 * to receive publish messages (see {@link com.canoo.dolphin.server.event.Message}).
 */
public interface MessageListener {

    /**
     * Method will be called whenever a message is received
     * @param message
     */
    void onMessage(Message message);
}
