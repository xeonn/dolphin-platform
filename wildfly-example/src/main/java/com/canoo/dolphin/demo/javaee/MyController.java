package com.canoo.dolphin.demo.javaee;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

import javax.inject.Inject;

@DolphinController
public class MyController {

    @Inject
    private BeanManager manager;

    @DolphinAction
    public void callMe() {}

    @DolphinAction
    public void actionCall() {}

}
