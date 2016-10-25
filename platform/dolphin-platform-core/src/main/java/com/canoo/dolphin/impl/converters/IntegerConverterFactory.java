package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class IntegerConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_INT = 3;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null ? null : ((Number) value).intValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return int.class.equals(cls) || Integer.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_INT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
