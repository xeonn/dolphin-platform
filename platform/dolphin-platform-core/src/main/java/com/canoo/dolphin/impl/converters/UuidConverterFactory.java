/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ValueConverterException;
import java.util.UUID;

/**
 *
 * @author onn
 */
public class UuidConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_UUID = 14;

    private final static Converter CONVERTER = new AbstractStringConverter<UUID>() {
        @Override
        public UUID convertFromDolphin(String value) throws ValueConverterException {
            try {
                return value == null ? null : UUID.fromString(value);
            } catch (Exception ex) {
                throw new ValueConverterException("Unable to parse UUID: " + value, ex);
            }
        }

        @Override
        public String convertToDolphin(UUID value) throws ValueConverterException {
            if (value == null)
                return null;
            
            return value.toString();
        }
    };

    @Override
    public boolean supportsType(Class<?> cls) {
        return UUID.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_UUID;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

}
