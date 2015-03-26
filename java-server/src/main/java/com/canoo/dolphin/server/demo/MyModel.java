package com.canoo.dolphin.server.demo;

import com.canoo.dolphin.mapping.*;


@DolphinBean("My-Type")
public class MyModel {

    @DolphinProperty
    private Property<String> DialogHeader;

    private Property<String> Name;

    public Property<String> getDialogHeader() {
        return DialogHeader;
    }

    public Property<String> getName() {
        return Name;
    }
}
