package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class BooleanConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_BOOLEAN = "b";

    private final static BooleanConverter CONVERTER = new BooleanConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return boolean.class.equals(cls) || Boolean.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_BOOLEAN;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
