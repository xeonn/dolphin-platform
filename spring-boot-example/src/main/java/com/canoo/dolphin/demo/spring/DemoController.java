package com.canoo.dolphin.demo.spring;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

import javax.inject.Inject;

@DolphinController
public class DemoController {

    @Inject
    private BeanManager manager;

    @DolphinAction
    public void callMe() {
    }

    @DolphinAction
    public void actionCall() {
    }

}
