package com.canoo.dolphin.server.demo;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.model.Attribute;
import com.canoo.dolphin.server.model.Model;

@Model("My-Type")
public class MyModel {

    @Attribute
    private Property<String> DialogHeader;

    @Attribute
    private Property<String> Name;

    @Attribute
    private Property<String> detail1;

    @Attribute
    private Property<String> detail2;

}
