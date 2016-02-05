package com.canoo.dolphin.test.demo;

import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;

@DolphinController("TestController")
public class TestController {

    @DolphinModel
    private TestModel model;

    @DolphinAction("action")
    public void doSomeAction() {
        model.setValue("Hello Dolphin Test");
    }
}
