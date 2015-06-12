package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.event.ValueChangeEvent;
import com.canoo.dolphin.event.ValueChangeListener;
import com.canoo.dolphin.server.DolphinAction;
import com.canoo.dolphin.server.DolphinController;
import com.canoo.dolphin.server.Param;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;

import javax.inject.Inject;
import java.util.List;

@DolphinController
public class ToDoController {

    @Inject
    private BeanManager beanManager;

    @Inject
    private ToDoItemRepository repository;

    private ToDoList toDoList;

    @DolphinAction
    public void init() {
        final List<ToDoItemDTO> items = repository.findAll();
        toDoList = beanManager.create(ToDoList.class);

        for (final ToDoItemDTO itemDTO : items) {
            final ToDoItem item = beanManager.create(ToDoItem.class);
            final long id = itemDTO.getId();
            toDoList.getItems().add(item);

            item.setEntityId(id);
            item.setText(itemDTO.getText());
            item.getTextProperty().onChanged(new ValueChangeListener<String>() {
                @Override
                public void valueChanged(ValueChangeEvent<? extends String> evt) {
                    final ToDoItemDTO itemDTO = repository.findOne(id);
                    itemDTO.setText(evt.getNewValue());
                    repository.save(itemDTO);
                }
            });

            item.setCompleted(itemDTO.isCompleted());
            item.getCompletedProperty().onChanged(new ValueChangeListener<Boolean>() {
                @Override
                public void valueChanged(ValueChangeEvent<? extends Boolean> evt) {
                    final ToDoItemDTO itemDTO = repository.findOne(id);
                    itemDTO.setCompleted(evt.getNewValue());
                    repository.save(itemDTO);
                }
            });
        }
    }

    @DolphinAction
    public void add(@Param("text") String text) {
        final ToDoItemDTO initItemDTO = new ToDoItemDTO();
        initItemDTO.setText(text);
        final ToDoItemDTO createdItemDTO = repository.save(initItemDTO);
        final ToDoItem toDoItem = beanManager.create(ToDoItem.class);
        toDoItem.setEntityId(createdItemDTO.getId());
        toDoItem.setText(text);
        toDoList.getItems().add(toDoItem);
    }

    @DolphinAction
    public void remove(@Param("item") ToDoItem item) {
        toDoList.getItems().remove(item);
        beanManager.remove(item);
        repository.delete(item.getEntityId());
    }

}
