package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.BeanRepository;

public interface ConverterFactory {

    void init(BeanRepository beanRepository);

    boolean supportsType(Class<?> cls);

    int getTypeIdentifier();

    Converter getConverterForType(Class<?> cls);

}
