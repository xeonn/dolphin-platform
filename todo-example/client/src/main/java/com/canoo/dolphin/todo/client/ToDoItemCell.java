package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.todo.pm.ToDoItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;

public class ToDoItemCell extends ListCell<ToDoItem> {

    private CheckBox doneCheckbox;

    public ToDoItemCell() {
        doneCheckbox = new CheckBox();
        doneCheckbox.visibleProperty().bind(emptyProperty().not());
        setGraphic(doneCheckbox);
    }

    @Override
    protected void updateItem(ToDoItem item, boolean empty) {
        super.updateItem(item, empty);
        if(item != null) {
            textProperty().bind(FXBinder.wrapStringProperty(item.getTextProperty()));
            doneCheckbox.selectedProperty().bindBidirectional(FXBinder.wrapBooleanProperty(item.getCompletedProperty()));
        }
    }
}
