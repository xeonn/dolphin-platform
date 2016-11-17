package org.opendolphin.binding;

import groovy.lang.Closure;

public class ConverterAdapter implements Converter {

    private Closure converter;

    public ConverterAdapter() {
    }

    public ConverterAdapter(Closure converter) {
        this.converter = converter;
    }

    @Override
    public Object convert(Object value) {
        return converter == null ? value : converter.call(value);
    }

    public Closure getConverter() {
        return converter;
    }

    public void setConverter(Closure converter) {
        this.converter = converter;
    }

}
