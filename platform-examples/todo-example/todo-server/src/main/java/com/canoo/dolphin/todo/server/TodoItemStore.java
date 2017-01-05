package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.event.DolphinEventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TodoItemStore {

    private final DolphinEventBus eventBus;

    private final Map<String, Boolean> items = new HashMap<>();

    public TodoItemStore(DolphinEventBus eventBus) {
        this.eventBus = eventBus;
    }

    void addItem(String name) {
        items.put(name, false);
        eventBus.publish(ToDoController.ITEM_ADDED, name);
    }

    void removeItem(String name) {
        items.remove(name);
        eventBus.publish(ToDoController.ITEM_REMOVED, name);
    }

    void changeItemState(String name) {
        items.put(name, !items.get(name));
        eventBus.publish(ToDoController.ITEM_MARK_CHANGED, name);
    }

    Stream<String> itemStream() {
        return items.keySet().stream();
    }

    boolean getState(String name) {
        return items.get(name);
    }
}
