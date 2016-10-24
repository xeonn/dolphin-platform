package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class FloatConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_FLOAT = "f";

    private final static FloatConverter CONVERTER = new FloatConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return float.class.equals(cls) || Float.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_FLOAT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
