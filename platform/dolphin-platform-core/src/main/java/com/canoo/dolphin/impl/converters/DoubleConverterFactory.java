package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class DoubleConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_DOUBLE = "d";

    private final static DoubleConverter CONVERTER = new DoubleConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return double.class.equals(cls) || Double.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_DOUBLE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
