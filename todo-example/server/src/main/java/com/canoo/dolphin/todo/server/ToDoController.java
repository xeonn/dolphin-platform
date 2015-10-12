package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.event.TaskExecutor;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@DolphinController("ToDoController")
public class ToDoController {

    @Inject
    private BeanManager beanManager;

    @Inject
    private TaskExecutor taskExecutor;

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
        final String newItemText = toDoList.getNewItemText().get();
        toDoList.getNewItemText().set("");

        onAdded(newItemText);
        // MH: For some reason this did not work for me
//        taskExecutor.execute(ToDoController.class, c -> c.onAdded(newItemText));
    }

    private void onAdded(String text) {
        final ToDoItem toDoItem = beanManager.create(ToDoItem.class);
        toDoItem.setText(text);
        toDoList.getItems().add(toDoItem);
    }
}
