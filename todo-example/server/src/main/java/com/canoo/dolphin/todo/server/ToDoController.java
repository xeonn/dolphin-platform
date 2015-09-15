package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@DolphinController("ToDoController")
public class ToDoController {

    @Inject
    private BeanManager beanManager;

    @DolphinModel
    private ToDoList toDoList;

    @PostConstruct
    public void onInit() {
        System.out.println("Init");
    }

    @PreDestroy
    public void onDestroy() {
        System.out.println("Destroyed");
    }

    @DolphinAction
    public void add() {
        final ToDoItem toDoItem = beanManager.create(ToDoItem.class);
        toDoItem.setText(toDoList.getNewItemText().get());
        toDoList.getItems().add(toDoItem);
        toDoList.getNewItemText().set("");
    }
}
