package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class ByteConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_BYTE = "B";

    private final static ByteConverter CONVERTER = new ByteConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return byte.class.equals(cls) || Byte.class.equals(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_BYTE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
