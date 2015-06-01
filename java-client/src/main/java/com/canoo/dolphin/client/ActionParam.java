package com.canoo.dolphin.client;

/**
 * Created by hendrikebbers on 31.03.15.
 */
@Deprecated
public class ActionParam {

    private String name;

    private Object value;

    public ActionParam(String name, Object value) {
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
