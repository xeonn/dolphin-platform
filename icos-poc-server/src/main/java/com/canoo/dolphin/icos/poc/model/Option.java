package com.canoo.dolphin.icos.poc.model;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean("Option")
public class Option {

    private Property<String> key;
    public String getKey() {
        return key.get();
    }
    public void setKey(String value) {
        key.set(value);
    }

    private Property<String> label;
    public String getLabel() {
        return label.get();
    }
    public void setLabel(String value) {
        label.set(value);
    }

}
