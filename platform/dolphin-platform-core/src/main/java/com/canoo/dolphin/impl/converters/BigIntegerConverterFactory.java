/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.converter.Converter;
import com.canoo.dolphin.converter.ValueConverterException;
import java.math.BigInteger;

/**
 *
 * @author onn
 */
public class BigIntegerConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_BIGINTEGER = 13;

    private final static Converter CONVERTER = new AbstractNumberConverter<BigInteger>() {
        @Override
        public BigInteger convertFromDolphin(Number value) throws ValueConverterException {
            try {
                return value == null ? null : new BigInteger(value.toString());
            } catch (Exception e) {
                throw new ValueConverterException("Unable to parse the number: " + value, e);
            }
        }

        @Override
        public Number convertToDolphin(BigInteger value) throws ValueConverterException {
            return value;
        }
    };

    @Override
    public boolean supportsType(Class<?> cls) {
        return BigInteger.class.equals(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_BIGINTEGER;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }
}
