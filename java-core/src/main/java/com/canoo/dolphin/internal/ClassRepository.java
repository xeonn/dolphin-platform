package com.canoo.dolphin.internal;

import com.canoo.dolphin.internal.info.ClassInfo;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface ClassRepository {

    ClassInfo getClassInfo(String modelType);

    ClassInfo getOrCreateClassInfo(final Class<?> beanClass);
}
