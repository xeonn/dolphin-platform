package com.canoo.dolphin.todo.pm;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.DolphinBean;

@DolphinBean
public class ToDoList {

    private ObservableList<ToDoItem> items;

    public ObservableList<ToDoItem> getItems() {
        return items;
    }

}
