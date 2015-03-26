package com.canoo.dolphin.server.demo;

import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class Demo {

    void bla() {

        ModelManager m = null;

        MyModel model = null;

        model.getName().addValueListener(new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                String e = evt.getNewValue();
            }
        });

    }

}
