package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class DoubleConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_DOUBLE = 6;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).doubleValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return double.class.equals(cls) || Double.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_DOUBLE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
