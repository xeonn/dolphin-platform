package com.canoo.dolphin.workflow.server.model;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("BaseProcessInstance")
public class BaseProcessInstance {
    private Property<String> label;
    public String getLabel() {
        return label.get();
    }

    public void setLabel(String id) {
        this.label.set(id);
    }
}
