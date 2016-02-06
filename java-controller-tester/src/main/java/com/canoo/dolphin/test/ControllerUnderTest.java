package com.canoo.dolphin.test;

import com.canoo.dolphin.client.Param;

public interface ControllerUnderTest<T> {

    T getModel();

    void invoke(String actionName, Param... params);

    void destroy();
}
