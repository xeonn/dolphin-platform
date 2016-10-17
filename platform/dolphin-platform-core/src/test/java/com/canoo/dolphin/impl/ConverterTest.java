package com.canoo.dolphin.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class ConverterTest {

    @Test
    public void testDateConversions() {
        Converters converters = new Converters(null);
        Converters.Converter converter = converters.getConverter(Date.class);

        Date testDate1 = new Date();
        checkConversion(converter, testDate1);

        Date testDate2 = new Date(0);
        checkConversion(converter, testDate2);

        Date testDate3 = new Date(Long.MAX_VALUE);
        checkConversion(converter, testDate3);

        Date testDate4 = null;
        checkConversion(converter, testDate4);

        //TODO: Not working based on date formate (yyyy), max year is 9999
        //Date testDate5 = new Date(Long.MIN_VALUE);
    }

    @Test
    public void testStringConversions() {
        Converters converters = new Converters(null);
        Converters.Converter converter = converters.getConverter(String.class);

        checkConversion(converter, "");
        checkConversion(converter, null);
        checkConversion(converter, "Hello");
    }

    @Test
    public void testDoubleConversions() {
        Converters converters = new Converters(null);
        Converters.Converter converter = converters.getConverter(Double.class);

        checkConversion(converter, 2.9d);
        checkConversion(converter, null);
        checkConversion(converter, 0.0);
        checkConversion(converter, Double.MAX_VALUE);
        checkConversion(converter, Double.MIN_VALUE);
    }

    private void checkConversion(Converters.Converter converter, Object val) {
        Object converted = converter.convertToDolphin(val);
        Object reconverted = converter.convertFromDolphin(converted);
        Assert.assertEquals(reconverted, val);
    }

}
