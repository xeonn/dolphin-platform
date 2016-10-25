/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.converters;

import com.canoo.dolphin.impl.Converter;
import com.canoo.dolphin.impl.converters.AbstractConverterFactory;
import com.canoo.dolphin.impl.converters.AbstractStringConverter;

import java.time.Period;

/**
 * Created by hendrikebbers on 25.10.16.
 */
public class PeriodConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new PeriodConverter();

    public final static int FIELD_TYPE = 53;

    @Override
    public boolean supportsType(Class<?> cls) {
        return Period.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class PeriodConverter extends AbstractStringConverter<Period> {

        @Override
        public Period convertFromDolphin(String value) {
            if(value == null) {
                return null;
            }
            return Period.parse(value);
        }

        @Override
        public String convertToDolphin(Period value) {
            if(value == null) {
                return null;
            }
            return value.toString();
        }
    }

}
