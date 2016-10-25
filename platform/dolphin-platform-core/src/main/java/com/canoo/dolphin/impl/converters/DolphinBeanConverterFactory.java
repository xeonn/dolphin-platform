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
package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;
import com.canoo.dolphin.impl.ConverterFactory;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.internal.BeanRepository;

public class DolphinBeanConverterFactory implements ConverterFactory {

    public final static int FIELD_TYPE_DOLPHIN_BEAN = 0;

    private DolphinBeanConverter converter;

    @Override
    public void init(BeanRepository beanRepository) {
        this.converter = new DolphinBeanConverter(beanRepository);
    }

    @Override
    public boolean supportsType(Class<?> cls) {
        return DolphinUtils.isDolphinBean(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_DOLPHIN_BEAN;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return converter;
    }

    private class DolphinBeanConverter extends AbstractStringConverter<Object> {

        private final BeanRepository beanRepository;

        public DolphinBeanConverter(BeanRepository beanRepository) {
            this.beanRepository = beanRepository;
        }

        @Override
        public Object convertFromDolphin(String value) {
            return beanRepository.getBean((String) value);
        }

        @Override
        public String convertToDolphin(Object value) {
            return beanRepository.getDolphinId(value);
        }
    }
}
