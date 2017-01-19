/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
import com.canoo.dolphin.server.event.MessageListener;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

import static com.canoo.dolphin.todo.TodoAppConstants.ADD_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CHANGE_ACTION;
import static com.canoo.dolphin.todo.TodoAppConstants.CONTROLLER_NAME;
import static com.canoo.dolphin.todo.TodoAppConstants.ITEM_PARAM;
import static com.canoo.dolphin.todo.TodoAppConstants.REMOVE_ACTION;
import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_ADDED;
import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_MARK_CHANGED;
import static com.canoo.dolphin.todo.server.ToDoEventTopics.ITEM_REMOVED;
import java.util.function.Consumer;
import java.util.function.Predicate;

@DolphinController(CONTROLLER_NAME)
public class ToDoController {

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
        eventBus.subscribe(ITEM_MARK_CHANGED, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                updateItemState(message.getData());
            }
        });
        eventBus.subscribe(ITEM_REMOVED, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                removeItem(message.getData());
            }
        });
        eventBus.subscribe(ITEM_ADDED, new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
                addItem(message.getData());
            }
        });
        todoItemStore.itemNameStream().forEach(new Consumer<String>() {
            @Override
            public void accept(String name) {
                addItem(name);
            }
        });
    }

    @DolphinAction(ADD_ACTION)
    public void onItemAddAction() {
        todoItemStore.addItem(toDoList.getNewItemText().get());
        toDoList.getNewItemText().set("");
    }

    @DolphinAction(CHANGE_ACTION)
    public void onItemStateChangedAction(@Param(ITEM_PARAM) String name) {
        todoItemStore.changeItemState(name);
    }

    @DolphinAction(REMOVE_ACTION)
    public void onItemRemovedAction(@Param(ITEM_PARAM) String name) {
        todoItemStore.removeItem(name);
    }

    private void addItem(String name) {
        toDoList.getItems().add(beanManager.create(ToDoItem.class).withText(name).withState(todoItemStore.getState(name)));
    }

    private void removeItem(String name) {
        getItemByName(name).ifPresent(new Consumer<ToDoItem>() {
            @Override
            public void accept(ToDoItem i) {
                toDoList.getItems().remove(i);
            }
        });
    }

    private void updateItemState(final String name) {
        getItemByName(name).ifPresent(new Consumer<ToDoItem>() {
            @Override
            public void accept(ToDoItem i) {
                i.setCompleted(todoItemStore.getState(name));
            }
        });
    }

    private Optional<ToDoItem> getItemByName(final String name) {
        return toDoList.getItems().stream().filter(new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem i) {
                return i.getText().equals(name);
            }
        }).findAny();
    }
}
