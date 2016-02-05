package com.canoo.dolphin.test;

import com.canoo.dolphin.client.Param;

public interface ControllerAccess<T> {

    T getModel();

    void invoke(String actionName, Param... params);

    void destroy();
}
