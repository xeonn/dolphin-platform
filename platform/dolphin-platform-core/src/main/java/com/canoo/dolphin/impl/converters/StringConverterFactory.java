package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class StringConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_STRING = "S";
    private final static StringConverter CONVERTER = new StringConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return String.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_STRING;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
