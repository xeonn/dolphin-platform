package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

import java.util.Date;

public class DateConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_DATE = "D";

    private final static DateConverter CONVERTER = new DateConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Date.class.isAssignableFrom(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_DATE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
