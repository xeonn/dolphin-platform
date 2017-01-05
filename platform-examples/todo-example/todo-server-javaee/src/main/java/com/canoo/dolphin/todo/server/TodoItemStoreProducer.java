package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.event.DolphinEventBus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Created by hendrikebbers on 05.01.17.
 */
public class TodoItemStoreProducer {

    @Produces
    @ApplicationScoped
    TodoItemStore createItemStore(DolphinEventBus eventBus) {
        TodoItemStore todoItemStore = new TodoItemStore();
        todoItemStore.setEventBus(eventBus);
        return todoItemStore;
    }

}
