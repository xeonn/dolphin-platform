package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EnumConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_ENUM = 10;

    private final Map<Class<?>, EnumConverter> enumConverters = new HashMap<>();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
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

    private static class EnumConverter implements Converter {

        private static final Logger LOG = LoggerFactory.getLogger(EnumConverter.class);

        private final Class<? extends Enum> clazz;

        @SuppressWarnings("unchecked")
        public EnumConverter(Class<?> clazz) {
            this.clazz = (Class<? extends Enum>) clazz;
        }

        @Override
        public Object convertFromDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return Enum.valueOf(clazz, value.toString());
            } catch (IllegalArgumentException ex) {
                LOG.warn("Unable to convert to an enum (%s): %s", clazz, value);
                return null;
            }
        }

        @Override
        public Object convertToDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return ((Enum)value).name();
            } catch (ClassCastException ex) {
                LOG.warn("Unable to evaluate the enum: " + value);
                return null;
            }
        }
    }

}
