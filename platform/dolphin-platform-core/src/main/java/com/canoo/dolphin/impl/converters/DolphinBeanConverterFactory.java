package com.canoo.dolphin.impl.converters;

import com.canoo.dolphin.impl.Converter;
import com.canoo.dolphin.impl.ConverterFactory;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.mapping.DolphinBean;

public class DolphinBeanConverterFactory implements ConverterFactory {

    public final static String FIELD_TYPE_DOLPHIN_BEAN = "O";

    private DolphinBeanConverter converter;

    @Override
    public void init(BeanRepository beanRepository) {
        this.converter = new DolphinBeanConverter(beanRepository);
    }

    @Override
    public boolean supportsType(Class<?> cls) {
        return cls.isAnnotationPresent(DolphinBean.class);
    }

    @Override
    public String getTypeIdentifier() {
        return FIELD_TYPE_DOLPHIN_BEAN;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return converter;
    }
}
