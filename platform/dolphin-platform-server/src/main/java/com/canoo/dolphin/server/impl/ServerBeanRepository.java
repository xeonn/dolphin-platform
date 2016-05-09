package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.internal.BeanRepository;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public interface ServerBeanRepository extends BeanRepository {

    <T> void deleteByGC(T bean);
}
