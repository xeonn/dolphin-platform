package com.canoo.dolphin.demo.spring;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

@DolphinController
public class DemoController {

    @DolphinAction
    public void callMe() {}

    @DolphinAction
    public void actionCall() {}

}
