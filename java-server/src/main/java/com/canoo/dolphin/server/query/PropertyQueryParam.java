package com.canoo.dolphin.server.query;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class PropertyQueryParam<T> {

    private String name;

    private ValueCheck<T> check;

    public PropertyQueryParam(String name, ValueCheck<T> check) {
        this.name = name;
        this.check = check;
    }

    public ValueCheck<T> getCheck() {
        return check;
    }

    public String getName() {
        return name;
    }
}
