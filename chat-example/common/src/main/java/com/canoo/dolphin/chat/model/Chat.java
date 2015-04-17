package com.canoo.dolphin.chat.model;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;

public class Chat {

    private Property<Long> entityId;

    private Property<ChatUser> chatPartner;

    private Property<Boolean> chatPartnerTyping;

    private ObservableList<ChatMessage> messages;

    public Long getEntityId() {
        return getEntityIdProperty().get();
    }

    public Property<Long> getEntityIdProperty() {
        return entityId;
    }

    public Property<ChatUser> getChatPartnerProperty() {
        return chatPartner;
    }

    public Property<Boolean> getChatPartnerTypingProperty() {
        return chatPartnerTyping;
    }

    public ObservableList<ChatMessage> getMessages() {
        return messages;
    }
}
