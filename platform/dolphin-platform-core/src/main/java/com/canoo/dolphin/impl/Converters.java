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
package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * The class {@code Converters} contains all {@link Converter} that are used in the Dolphin Platform.
 */
public class Converters {

    private static final Logger LOG = LoggerFactory.getLogger(Converters.class);

    private static final Converter IDENTITY_CONVERTER = new BaseConverter();

    private static final Converter BYTE_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).byteValue();
        }
    };

    private static final Converter SHORT_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).shortValue();
        }
    };

    private static final Converter INTEGER_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).intValue();
        }
    };

    private static final Converter LONG_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).longValue();
        }
    };

    private static final Converter FLOAT_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).floatValue();
        }
    };

    private static final Converter DOUBLE_CONVERTER = new BaseConverter() {
        @Override
        public Object convertFromDolphin(Object value) {
            return value == null? null : ((Number)value).doubleValue();
        }
    };

    private final Converter dateConverter = new DateConverter();

    private final Converter calendarConverter = new CalendarConverter();

    private final Converter dolphinBeanConverter;

    private final Map<Class<?>, Converter> enumConverters = new HashMap<>();

    public Converters(BeanRepository beanRepository) {
        this.dolphinBeanConverter = new DolphinBeanConverter(beanRepository);
    }

    public Converter getConverter(Class<?> clazz) {
        Assert.requireNonNull(clazz, "clazz");
        final ClassRepositoryImpl.FieldType type = DolphinUtils.getFieldType(clazz);
        switch (type) {
            default:
                return dolphinBeanConverter;
            case BYTE:
                return BYTE_CONVERTER;
            case SHORT:
                return SHORT_CONVERTER;
            case INT:
                return INTEGER_CONVERTER;
            case LONG:
                return LONG_CONVERTER;
            case FLOAT:
                return FLOAT_CONVERTER;
            case DOUBLE:
                return DOUBLE_CONVERTER;
            case BOOLEAN:
            case STRING:
                return IDENTITY_CONVERTER;
            case DATE:
                return Date.class.isAssignableFrom(clazz)? dateConverter : calendarConverter;
            case ENUM:
                Converter enumConverter = enumConverters.get(clazz);
                if (enumConverter == null) {
                    enumConverter = new EnumConverter(clazz);
                    enumConverters.put(clazz, enumConverter);
                }
                return enumConverter;
        }
    }

    private static class BaseConverter implements Converter {
        @Override
        public Object convertFromDolphin(Object value) {
            return value;
        }
        @Override
        public Object convertToDolphin(Object value) {
            return value;
        }
    }

    private static class DolphinBeanConverter implements Converter {
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

    private static class EnumConverter implements Converter {

        private final Class<? extends Enum> clazz;

        @SuppressWarnings("unchecked")
        private EnumConverter(Class<?> clazz) {
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

    private static class DateConverter implements Converter {

        private final DateFormat dateFormat;

        private DateConverter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public Object convertFromDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.parse(value.toString());
            } catch (ParseException e) {
                LOG.warn("Unable to parse the date: " + value);
                return null;
            }
        }

        @Override
        public Object convertToDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.format(value);
            } catch (IllegalArgumentException ex) {
                LOG.warn("Unable to format the date: " + value);
                return null;
            }
        }
    }

    private static class CalendarConverter implements Converter {

        private final DateFormat dateFormat;

        private CalendarConverter() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public Object convertFromDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                result.setTime(dateFormat.parse(value.toString()));
                return result;
            } catch (ParseException e) {
                LOG.warn("Unable to parse the date: " + value);
                return null;
            }
        }

        @Override
        public Object convertToDolphin(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return dateFormat.format(((Calendar)value).getTime());
            } catch (IllegalArgumentException ex) {
                LOG.warn("Unable to format the date: " + value);
                return null;
            }
        }
    }
}
