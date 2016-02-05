package com.canoo.dolphin.test;

public interface ControllerTest {

    <T> ControllerAccess<T> createControllerProxy(String controllerName);

}
