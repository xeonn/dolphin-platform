package com.canoo.dolphin.test.future;

public interface ControllerWrapper<C, M> {

    C getController();

    M getModel();

    void destroy();

}
