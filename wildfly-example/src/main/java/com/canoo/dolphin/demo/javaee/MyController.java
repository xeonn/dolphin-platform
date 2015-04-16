package com.canoo.dolphin.demo.javaee;

import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.BeanManagerImpl;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import org.opendolphin.core.server.ServerDolphin;

import javax.annotation.PostConstruct;
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
