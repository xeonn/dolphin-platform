package com.canoo.dolphin.server.util;

import com.canoo.dolphin.mapping.Property;

public class EnumDataTypesModel {

    public enum DataType {TEST_VALUE_1, TEST_VALUE_2, TEST_VALUE_3}

    private Property<DataType> enumProperty;

    public Property<DataType> getEnumProperty() {
        return enumProperty;
    }
}
