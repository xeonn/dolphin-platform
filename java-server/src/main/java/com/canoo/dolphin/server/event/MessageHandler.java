package com.canoo.dolphin.server.event;

/**
 * A handler that can be registered to the dolphin message bus to receive publish messages
 */
public interface MessageHandler {

    /**
     * Method will be called whenever a message is received
     * @param message
     */
    void onMessage(Message message);
}
