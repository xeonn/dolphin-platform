package com.canoo.dolphin.server.query;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class NotEqualsValueCheck<T> implements ValueCheck<T> {

    private T baseValue;

    public NotEqualsValueCheck(T baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public boolean check(T value) {
        if(baseValue == null && value == null) {
            return false;
        }
        if(baseValue != null && value == null) {
            return true;
        }
        if(baseValue == null && value != null) {
            return true;
        }
        return !baseValue.equals(value);
    }
}
