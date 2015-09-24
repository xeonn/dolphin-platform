package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 15.09.15.
 */
public interface ControllerTask<T> {

    void run(T controller);
}
