package com.canoo.dolphin.test;

public interface ControllerTest {

    <T> ControllerUnderTest<T> createController(String controllerName);

}
