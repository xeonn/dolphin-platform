package com.canoo.dolphin.server.util;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 30.03.15.
 */
@DolphinBean("simple_test_model")
public class SimpleAnnotatedTestModel {

    @DolphinProperty("text_property")
    private Property<String> text;

    public Property<String> getTextProperty() {
        return text;
    }

}
