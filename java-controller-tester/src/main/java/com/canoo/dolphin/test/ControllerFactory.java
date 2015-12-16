package com.canoo.dolphin.test;

public interface ControllerFactory {

    <C, M> ControllerWrapper<C, M> create(Class<C> controllerClass);

}
