package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class ByteConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_BYTE = 1;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null ? null : ((Number) value).byteValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return byte.class.equals(cls) || Byte.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BYTE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
