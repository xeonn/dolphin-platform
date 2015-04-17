package com.canoo.dolphin.chat.model;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class ChatMessage {

    private Property<String> message;

    private Property<Long> timeStamp;

    private Property<ChatUser> sender;

    public Property<ChatUser> getSenderProperty() {
        return sender;
    }

    public Property<String> getMessageProperty() {
        return message;
    }

    public Property<Long> getTimeStampProperty() {
        return timeStamp;
    }
}
