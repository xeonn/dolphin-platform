package com.canoo.dolphin.server.util;

import com.canoo.dolphin.mapping.Property;

public class EnumDataTypesModel {

    public enum DataType {VALUE_1, VALUE_2, VALUE_3}

    private Property<DataType> enumProperty;

    public Property<DataType> getEnumProperty() {
        return enumProperty;
    }
}
