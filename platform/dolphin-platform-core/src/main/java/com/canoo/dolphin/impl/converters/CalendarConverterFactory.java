package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new CalendarConverter();

    private final static int FIELD_TYPE_CALENDAR = 11;

    @Override
    public boolean supportsType(Class<?> cls) {
        return Calendar.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_CALENDAR;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class CalendarConverter implements Converter {

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
}
