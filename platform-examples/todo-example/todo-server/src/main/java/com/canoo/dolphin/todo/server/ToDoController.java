/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.DolphinModel;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.Message;
import com.canoo.dolphin.server.event.Topic;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Function;

import static com.canoo.dolphin.todo.TodoAppConstants.ADD_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CHANGE_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CONTROLLER_NAME;
import static com.canoo.dolphin.todo.TodoAppConstants.REMOVE_ACTION;

@DolphinController(CONTROLLER_NAME)
public class ToDoController {

    public final static Topic<String> ITEM_MARK_CHANGED = Topic.create("item_mark_changed");

    public final static Topic<String> ITEM_ADDED = Topic.create("item_added");

    public final static Topic<String> ITEM_REMOVED = Topic.create("item_removed");

    @Inject
    private BeanManager beanManager;

    @Inject
    private DolphinEventBus eventBus;

    @Inject
    private TodoItemStore todoItemStore;

    @DolphinModel
    private ToDoList toDoList;

    @PostConstruct
    public void onInit() {
        Function<Message<String>, Optional<ToDoItem>> getItemByName = m -> toDoList.getItems().stream().
                filter(i -> i.getText().equals(m.getData())).findAny();
        eventBus.subscribe(ITEM_MARK_CHANGED, m -> getItemByName.apply(m).ifPresent(i -> i.setCompleted(todoItemStore.getState(i.getText()))));
        eventBus.subscribe(ITEM_REMOVED, m -> getItemByName.apply(m).ifPresent(i -> toDoList.getItems().remove(i)));
        eventBus.subscribe(ITEM_ADDED, m -> toDoList.getItems().add(beanManager.create(ToDoItem.class).withText(m.getData())));
        todoItemStore.itemStream().forEach(name -> toDoList.getItems().add(beanManager.create(ToDoItem.class).withText(name).withState(todoItemStore.getState(name))));
    }

    @DolphinAction(ADD_ACTION)
    public void add() {
        todoItemStore.addItem(toDoList.getNewItemText().get());
        toDoList.getNewItemText().set("");
    }

    @DolphinAction(CHANGE_ACTION)
    public void markChanged(@Param("itemName") String name) {
        todoItemStore.changeItemState(name);
    }

    @DolphinAction(REMOVE_ACTION)
    public void remove(@Param("itemName") String name) {
        todoItemStore.removeItem(name);
    }
}
