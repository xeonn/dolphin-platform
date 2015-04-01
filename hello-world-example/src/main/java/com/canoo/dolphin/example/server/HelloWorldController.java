package com.canoo.dolphin.example.server;

import com.canoo.dolphin.example.HelloWorldModel;
import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

import javax.inject.Inject;

@DolphinController("hello-world")
public class HelloWorldController {

    @Inject
    private BeanManager manager;

    @DolphinAction("init")
    public void init() {
        HelloWorldModel model = manager.create(HelloWorldModel.class);
        model.getTextProperty().set("Hello World");
    }
}
