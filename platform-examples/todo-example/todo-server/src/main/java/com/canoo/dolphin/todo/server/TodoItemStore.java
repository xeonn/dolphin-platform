package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.event.DolphinEventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_ADDED;
import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_MARK_CHANGED;
import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_REMOVED;

public class TodoItemStore {

    private DolphinEventBus eventBus;

    private final Map<String, Boolean> items = new HashMap<>();

    public void setEventBus(DolphinEventBus eventBus) {
        this.eventBus = eventBus;
    }

    void addItem(String name) {
        if(name != null && !name.isEmpty() && !items.containsKey(name)) {
            items.put(name, false);
            eventBus.publish(ITEM_ADDED, name);
        }
    }

    void removeItem(String name) {
        items.remove(name);
        eventBus.publish(ITEM_REMOVED, name);
    }

    void changeItemState(String name) {
        items.put(name, !items.get(name));
        eventBus.publish(ITEM_MARK_CHANGED, name);
    }

    Stream<String> itemNameStream() {
        return items.keySet().stream();
    }

    boolean getState(String name) {
        return items.get(name);
    }
}
