package com.canoo.dolphin.test.qualifier;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class QualifierTestModel {

    private Property<QualifierTestSubModelOne> subModelOneProperty;

    private Property<QualifierTestSubModelTwo> subModelTwoProperty;

    public Property<QualifierTestSubModelOne> subModelOneProperty() {
        return subModelOneProperty;
    }

    public Property<QualifierTestSubModelTwo> subModelTwoProperty() {
        return subModelTwoProperty;
    }
}
