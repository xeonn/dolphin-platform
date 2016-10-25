package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

public class ShortConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_SHORT = 2;

    private final static Converter CONVERTER = new DirectConverter() {

        @Override
        public Object convertFromDolphin(Object value) {
            return value == null ? null : ((Number) value).shortValue();
        }
    };


    @Override
    public boolean supportsType(Class<?> cls) {
        return short.class.equals(cls) || Short.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_SHORT;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
