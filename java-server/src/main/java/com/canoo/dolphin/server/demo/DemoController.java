package com.canoo.dolphin.server.demo;

import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import com.canoo.dolphin.server.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;

/**
 * Created by hendrikebbers on 26.03.15.
 */
@DolphinController
public class DemoController {

    //@Inject
    private BeanManager manager;

    @DolphinAction
    void init() {
        MyModel model = manager.create(MyModel.class);

        model.getNameProperty().addValueListener(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                System.out.println("Value Changed!");
            }
        });
    }
}
