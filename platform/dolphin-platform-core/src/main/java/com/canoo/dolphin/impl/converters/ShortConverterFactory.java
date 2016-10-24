package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class ShortConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_SHORT = "s";

    private final static ShortConverter CONVERTER = new ShortConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return short.class.equals(cls) || Short.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_SHORT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
