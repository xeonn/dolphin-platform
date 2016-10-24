package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

import java.util.Calendar;

public class CalendarConverterFactory extends AbstractConverterFactory {

    private final static CalendarConverter CONVERTER = new CalendarConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Calendar.class.isAssignableFrom(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return DateConverterFactory.FIELD_TYPE_DATE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
