package com.canoo.dolphin.test.eventbus;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class EventBusTestModel {

    private Property<String> value;

    public Property<String> valueProperty() {
        return value;
    }
}