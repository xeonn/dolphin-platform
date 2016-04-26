/**
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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.BeanRepository;

/**
 * The class {@code Converters} contains all {@link Converter} that are used in the Dolphin Platform.
 */
public class Converters {

    private static final Converter IDENTITY_CONVERTER = new Converter();

    private static final Converter BYTE_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).byteValue();
        }
    };

    private static final Converter SHORT_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).shortValue();
        }
    };

    private static final Converter INTEGER_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).intValue();
        }
    };

    private static final Converter LONG_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).longValue();
        }
    };

    private static final Converter FLOAT_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).floatValue();
        }
    };

    private static final Converter DOUBLE_CONVERTER = new Converter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).doubleValue();
        }
    };

    private final Converter dolphinBeanConverter;

    public Converters(BeanRepository beanRepository) {
        this.dolphinBeanConverter = new DolphinBeanConverter(beanRepository);
    }

    public Converter getConverter(Class<?> clazz) {
        if (String.class.equals(clazz) || boolean.class.equals(clazz) || Boolean.class.equals(clazz)) {
            return IDENTITY_CONVERTER;
        }
        if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return INTEGER_CONVERTER;
        }
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return LONG_CONVERTER;
        }
        if (double.class.equals(clazz) || Double.class.equals(clazz)) {
            return DOUBLE_CONVERTER;
        }
        if (float.class.equals(clazz) || Float.class.equals(clazz)) {
            return FLOAT_CONVERTER;
        }
        if (byte.class.equals(clazz) || Byte.class.equals(clazz)) {
            return BYTE_CONVERTER;
        }
        if (short.class.equals(clazz) || Short.class.equals(clazz)) {
            return SHORT_CONVERTER;
        }
        return dolphinBeanConverter;
    }

    public static class Converter {
        public Object convertFromDolphin(Object value) {
            return value;
        }
        public Object convertToDolphin(Object value) {
            return value;
        }
    }

    private static class DolphinBeanConverter extends Converter {
        private final BeanRepository beanRepository;

        private DolphinBeanConverter(BeanRepository beanRepository) {
            this.beanRepository = beanRepository;
        }

        @Override
        public Object convertFromDolphin(Object value) {
            return beanRepository.getBean((String) value);
        }

        @Override
        public Object convertToDolphin(Object value) {
            return beanRepository.getDolphinId(value);
        }
    }
}
