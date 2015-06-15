package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.inject.Inject;

@DolphinController("ToDoController")
public class ToDoController {

    @Inject
    private BeanManager beanManager;

    private ToDoList toDoList;

    @DolphinAction
    public void init() {
        toDoList = beanManager.create(ToDoList.class);
    }

    @DolphinAction
    public void destroy() {
        beanManager.remove(toDoList);
    }

    @DolphinAction
    public void add() {
        final ToDoItem toDoItem = beanManager.create(ToDoItem.class);
        toDoItem.setText(toDoList.getNewItemText().get());
        toDoList.getItems().add(toDoItem);
        toDoList.getNewItemText().set("");
    }
}
