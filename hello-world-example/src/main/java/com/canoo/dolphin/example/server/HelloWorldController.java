package com.canoo.dolphin.example.server;

import com.canoo.dolphin.example.HelloWorldModel;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

import javax.inject.Inject;

@DolphinController("hello-world")
public class HelloWorldController {

    @Inject
    private BeanManagerImpl manager;

    @DolphinAction("init")
    public void init() {
        HelloWorldModel model = manager.create(HelloWorldModel.class);
        model.getTextProperty().set("Hello World");
        model.getTextProperty().onChanged(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println("Client changed Text: " + evt.getNewValue());
            }
        });
    }
}
