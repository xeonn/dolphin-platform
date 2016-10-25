package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class LongConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_LONG = 4;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null ? null : ((Number) value).longValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return long.class.equals(cls) || Long.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_LONG;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
