package com.canoo.dolphin.test.future;

public interface ControllerFactory {

    <C, M> ControllerWrapper<C, M> create(Class<C> controllerClass);

}
