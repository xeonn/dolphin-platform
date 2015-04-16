package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.mapping.Property;

public class BaseProcessInstance {
    private Property<String> id;
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }
}
