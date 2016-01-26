package com.canoo.dolphin.validation;

import com.canoo.dolphin.impl.MockedProperty;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@DolphinBean
public class TestBean {

    @NotNull
    private Property<String> value1 = new MockedProperty<>();

    @Null
    private Property<String> value2 = new MockedProperty<>();

    @AssertTrue
    private Property<Boolean> value3 = new MockedProperty<>();

    @AssertFalse
    private Property<Boolean> value4 = new MockedProperty<>();

    public Property<String> value1Property() {
        return value1;
    }

    public Property<String> value2Property() {
        return value2;
    }

    public Property<Boolean> value3Property() {
        return value3;
    }

    public Property<Boolean> value4Property() {
        return value4;
    }
}
