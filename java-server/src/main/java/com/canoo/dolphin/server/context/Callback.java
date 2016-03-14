package com.canoo.dolphin.server.context;

/**
 * Created by hendrikebbers on 14.03.16.
 */
public interface Callback<T> {

    void call(T t);

}
