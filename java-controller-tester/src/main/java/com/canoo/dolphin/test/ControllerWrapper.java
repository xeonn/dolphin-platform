package com.canoo.dolphin.test;

public interface ControllerWrapper<C, M> {

    C getController();

    M getModel();

    void destroy();

}
