package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class FloatConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_FLOAT = 5;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null ? null : ((Number) value).floatValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return float.class.equals(cls) || Float.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_FLOAT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
