package com.canoo.dolphin.demo.spring;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import org.opendolphin.core.server.ServerDolphin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DolphinController
public class DemoController {

    @Inject
    private BeanManager manager;

    @Inject
    private ServerDolphin dolphin;

    @PostConstruct
    public void bla() {
        System.out.println("");
    }

    @DolphinAction
    public void callMe() {}

    @DolphinAction
    public void actionCall() {}

}
