package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class Param {

    private final String name;

    private final Object value;

    public Param(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
