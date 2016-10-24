package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class LongConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_LONG = "l";

    private final static LongConverter CONVERTER = new LongConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return long.class.equals(cls) || Long.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_LONG;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
