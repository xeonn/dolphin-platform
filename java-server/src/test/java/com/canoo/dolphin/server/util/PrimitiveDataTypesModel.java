package com.canoo.dolphin.server.util;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class PrimitiveDataTypesModel {

    private Property<String> textProperty;

    private Property<Integer> integerProperty;

    private Property<Double> doubleProperty;

    private Property<Float> floatProperty;

    private Property<Long> longProperty;

    private Property<Boolean> booleanProperty;

    private Property<Byte> byteProperty;

    private Property<Short> shortProperty;

    public Property<String> getTextProperty() {
        return textProperty;
    }

    public Property<Integer> getIntegerProperty() {
        return integerProperty;
    }

    public Property<Double> getDoubleProperty() {
        return doubleProperty;
    }

    public Property<Float> getFloatProperty() {
        return floatProperty;
    }

    public Property<Long> getLongProperty() {
        return longProperty;
    }

    public Property<Boolean> getBooleanProperty() {
        return booleanProperty;
    }

    public Property<Byte> getByteProperty() {
        return byteProperty;
    }

    public Property<Short> getShortProperty() {
        return shortProperty;
    }
}
