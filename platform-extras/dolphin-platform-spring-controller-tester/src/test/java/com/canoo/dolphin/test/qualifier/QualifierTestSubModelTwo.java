package com.canoo.dolphin.test.qualifier;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class QualifierTestSubModelTwo {

    private Property<Boolean> booleanProperty;

    private Property<Integer> integerProperty;

    private Property<String> stringProperty;

    public Property<Boolean> booleanProperty() {
        return booleanProperty;
    }

    public Property<Integer> integerProperty() {
        return integerProperty;
    }

    public Property<String> stringProperty() {
        return stringProperty;
    }
}
