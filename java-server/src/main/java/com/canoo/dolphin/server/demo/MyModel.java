package com.canoo.dolphin.server.demo;

import com.canoo.dolphin.mapping.*;


@DolphinBean("My-Type")
public class MyModel {

    private Property<String> name;

    public Property<String> getNameProperty() {
        return name;
    }
}
