package com.canoo.dolphin.server.query;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public interface ValueCheck<T> {

    boolean check(T value);

}
