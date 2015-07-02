package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToDoClient extends Application {

    private final ClientBeanManager beanManager = ClientBeanManager.create("http://localhost:8080/todo-app/dolphin");

    @Override
    public void start(Stage primaryStage) throws Exception {
        final TextField createField = new TextField();
        final Button createButton = new Button("Create");
        final HBox createComponent = new HBox(10, createField, createButton);
        final ListView<ToDoItem> itemList = new ListView<>();
         itemList.setCellFactory(c -> new ToDoItemCell());
        final VBox vBox = new VBox(10, createComponent, itemList);
        final StackPane root = new StackPane(vBox);


        beanManager.onAdded(ToDoList.class, list -> {

            StringProperty newItemTextProperty = FXBinder.wrapStringProperty(list.getNewItemText());
            createField.textProperty().bindBidirectional(newItemTextProperty);


            ObservableList<ToDoItem> items = FXBinder.wrapList(list.getItems());
            itemList.setItems(items);


            createButton.setOnAction(event -> beanManager.send("ToDoController:add"));
        });
        beanManager.send("ToDoController:init");


        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
