package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;

import java.util.HashMap;
import java.util.Map;

public class EnumConverterFactory extends AbstractConverterFactory {

    public final static String FIELD_TYPE_ENUM = "E";

    private final Map<Class<?>, EnumConverter> enumConverters = new HashMap<>();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_ENUM;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        EnumConverter enumConverter = enumConverters.get(cls);
        if (enumConverter == null) {
            enumConverter = new EnumConverter(cls);
            enumConverters.put(cls, enumConverter);
        }
        return enumConverter;
    }
}
