package com.canoo.dolphin.todo.pm;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.Property;

@DolphinBean
public class ToDoList {

    private ObservableList<ToDoItem> items;

    private Property<String> newItemText;

    public ObservableList<ToDoItem> getItems() {
        return items;
    }

    public Property<String> getNewItemText() {
        return newItemText;
    }
}
