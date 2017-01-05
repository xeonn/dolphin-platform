package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.event.DolphinEventBus;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * Created by hendrikebbers on 05.01.17.
 */
public class TodoItemStoreProducer {

    @Produces
    @Singleton
    public TodoItemStore createItemStore(DolphinEventBus eventBus) {
        return new TodoItemStore(eventBus);
    }

}
