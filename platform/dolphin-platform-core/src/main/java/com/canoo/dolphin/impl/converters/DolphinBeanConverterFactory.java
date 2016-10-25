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

    private class DolphinBeanConverter implements Converter {

        private final BeanRepository beanRepository;

        public DolphinBeanConverter(BeanRepository beanRepository) {
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
}
