package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ControllerProxy;

/**
 * Created by hendrikebbers on 18.02.16.
 */
public interface ControllerProxyFactory {

    <T> ControllerProxy<T> create(String name) throws Exception;
}
