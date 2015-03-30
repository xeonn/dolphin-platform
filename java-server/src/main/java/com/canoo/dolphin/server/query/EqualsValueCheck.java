package com.canoo.dolphin.server.query;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class EqualsValueCheck<T> implements ValueCheck<T> {

    private T baseValue;

    public EqualsValueCheck(T baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public boolean check(T value) {
        if(baseValue == null && value == null) {
            return true;
        }
        if(baseValue != null && value == null) {
            return false;
        }
        if(baseValue == null && value != null) {
            return false;
        }
        return baseValue.equals(value);
    }
}
