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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by hendrikebbers on 24.10.16.
 */
public class CalendarConverter implements Converter {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarConverter.class);

    private final DateFormat dateFormat;

    public CalendarConverter() {
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