package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_DATE = 9;

    private final static Converter CONVERTER = new DateConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Date.class.isAssignableFrom(cls);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_DATE;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class DateConverter implements Converter {

        private static final Logger LOG = LoggerFactory.getLogger(DateConverter.class);

        private final DateFormat dateFormat;

        public DateConverter() {
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
}
