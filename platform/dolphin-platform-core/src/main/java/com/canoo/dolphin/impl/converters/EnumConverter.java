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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumConverter implements Converter {

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
