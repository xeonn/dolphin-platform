package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.mapping.Property;

public class PropertyReference extends Reference {

    private Property property;

    public PropertyReference(Instance parent, Property property, Instance child) {
        super(parent, child);
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
