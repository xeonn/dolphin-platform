package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.AbstractViewController;
import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by hendrikebbers on 16.09.15.
 */
public class ToDoViewController extends AbstractViewController<ToDoList> {

    private final TextField createField;
    private final Button createButton;
    private final ListView<ToDoItem> itemList;
    private final StackPane root;

    public ToDoViewController(ClientContext clientContext) {
        super(clientContext, "ToDoController");

        createField = new TextField();
        createButton = new Button("Create");
        createButton.setDisable(true);
        HBox createComponent = new HBox(createField, createButton);
        itemList = new ListView<>();
        VBox vBox = new VBox(createComponent, itemList);
        root = new StackPane(vBox);
        itemList.setCellFactory(c -> new ToDoItemCell());
    }


    public StackPane getRoot() {
        return root;
    }

    @Override
    protected void init() {
        FXBinder.bindBidirectional(createField.textProperty(), getModel().getNewItemText());
        ObservableList<ToDoItem> items = FXBinder.wrapList(getModel().getItems());
        itemList.setItems(items);
        createButton.setDisable(false);
        createButton.setOnAction(event -> invoke("add"));
    }

    @Override
    protected void onInvocationException(Throwable t) {
        createField.setText("ERROR!!!!!!!!!");
    }
}
