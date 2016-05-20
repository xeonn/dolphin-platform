package com.canoo.dolphin.test;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class TestSubModel {

    private Property<String> value;

    public Property<String> valueProperty() {
        return value;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

}
