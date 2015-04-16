package com.canoo.dolphin.server.query;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class NotNullValueCheck<T> implements ValueCheck<T> {

    @Override
    public boolean check(T value) {
        return value != null;
    }
}

