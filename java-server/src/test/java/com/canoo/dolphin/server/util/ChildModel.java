package com.canoo.dolphin.server.util;

import com.canoo.dolphin.mapping.Property;

public class ChildModel extends ParentModel {

    private Property<String> childProperty;

    public Property<String> getChildProperty() {
        return childProperty;
    }
}
