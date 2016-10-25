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

import java.time.Duration;

public class DurationConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new DurationConverter();

    public final static int FIELD_TYPE = 51;

    @Override
    public boolean supportsType(Class<?> cls) {
        return Duration.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class DurationConverter extends AbstractStringConverter<Duration> {

        @Override
        public Duration convertFromDolphin(String value) {
            if(value == null) {
                return null;
            }
            return Duration.parse(value);
        }

        @Override
        public String convertToDolphin(Duration value) {
            if(value == null) {
                return null;
            }
            return value.toString();
        }
    }

}
