package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.ClientBeanManager;
import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.todo.pm.ToDoItem;
import com.canoo.dolphin.todo.pm.ToDoList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ToDoClient extends Application {

    private final ClientBeanManager beanManager = ClientBeanManager.create("http://localhost:8080/dolphin");

    @Override
    public void start(Stage primaryStage) throws Exception {
        beanManager.send("com.canoo.dolphin.todo.server.ToDoController:init");
        final TextField createField = new TextField();
        final Button createButton = new Button("Create");
        createButton.setOnAction(event -> beanManager.send("com.canoo.dolphin.todo.server.ToDoController:add", new ClientBeanManager.Param("text", createField.getText())));
        final HBox createComponent = new HBox(10, createField, createButton);

        final ListView<ToDoItem> itemList = new ListView<>();
        beanManager.onAdded(ToDoList.class, list -> list.getItems().onChanged(event -> {
            for (final ListChangeEvent.Change change : event.getChanges()) {
                final int start = change.getFrom();
                final int end = start + change.getRemovedElements().size();
                final List<ToDoItem> slice = itemList.getItems().subList(start, end);
                slice.clear();
                for (final ToDoItem item : event.getSource().subList(start, change.getTo())) {
                    slice.add(item);
                }
            }
        }));

        final VBox vBox = new VBox(10, createComponent, itemList);
        final StackPane root = new StackPane(vBox);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
