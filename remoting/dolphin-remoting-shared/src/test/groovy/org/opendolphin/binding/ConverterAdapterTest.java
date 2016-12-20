package org.opendolphin.binding;

import groovy.lang.Closure;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConverterAdapterTest {

    @Test
    public void testConvertEmpty() {
        ConverterAdapter converter = new ConverterAdapter();
        Assert.assertEquals(converter.convert("test"), "test");
    }

    @Test
    public void testConvertClosure() {
        ConverterAdapter converter = new ConverterAdapter(new Closure(this, this) {
            public String doCall(String value) {
                return "***" + value + "***";
            }

        });
        Assert.assertEquals(converter.convert("test"), "***test***");
    }

}
