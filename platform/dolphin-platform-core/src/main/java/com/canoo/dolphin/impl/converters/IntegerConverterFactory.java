package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class IntegerConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_INT = "i";

    private final static IntegerConverter CONVERTER = new IntegerConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return int.class.equals(cls) || Integer.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_INT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
