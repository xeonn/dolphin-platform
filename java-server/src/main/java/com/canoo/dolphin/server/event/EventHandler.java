package com.canoo.dolphin.server.event;

/**
 * Created by hendrikebbers on 16.04.15.
 */
public interface EventHandler<T> {

    void onEvent(Event<T> value);
}
