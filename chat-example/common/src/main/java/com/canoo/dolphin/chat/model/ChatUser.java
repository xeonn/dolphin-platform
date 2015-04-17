package com.canoo.dolphin.chat.model;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public class ChatUser {

    private Property<Long> entityId;

    private Property<String> name;

    private Property<State> state;

    public Property<Long> getEntityIdProperty() {
        return entityId;
    }

    public Property<String> getNameProperty() {
        return name;
    }

    public Property<State> getStateProperty() {
        return state;
    }
}
