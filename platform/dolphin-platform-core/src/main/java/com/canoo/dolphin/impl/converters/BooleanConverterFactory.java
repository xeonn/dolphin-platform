package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class BooleanConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_BOOLEAN = 7;

    private final static Converter CONVERTER = new DirectConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return boolean.class.equals(cls) || Boolean.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BOOLEAN;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
