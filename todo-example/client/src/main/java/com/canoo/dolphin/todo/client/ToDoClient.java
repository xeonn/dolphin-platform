package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.javafx.FXBinder;
import com.canoo.dolphin.client.javafx.JavaFXConfiguration;
import com.canoo.dolphin.client.v2.ClientContext;
import com.canoo.dolphin.client.v2.ClientContextFactory;
import com.canoo.dolphin.client.v2.ControllerProxy;
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

import java.util.concurrent.CompletableFuture;

public class ToDoClient extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        final TextField createField = new TextField();
        final Button createButton = new Button("Create");
        final HBox createComponent = new HBox(10, createField, createButton);
        final ListView<ToDoItem> itemList = new ListView<>();
        itemList.setCellFactory(c -> new ToDoItemCell());
        final VBox vBox = new VBox(10, createComponent, itemList);
        final StackPane root = new StackPane(vBox);

        ClientContext context = ClientContextFactory.connect(new JavaFXConfiguration("http://localhost:8080/dolphin"));

        CompletableFuture<ControllerProxy<ToDoList>> promise = context.createController("ToDoController");
        promise.thenAccept(c -> {
            ToDoList model = c.getModel();
            StringProperty newItemTextProperty = FXBinder.wrapStringProperty(model.getNewItemText());
            createField.textProperty().bindBidirectional(newItemTextProperty);
            ObservableList<ToDoItem> items = FXBinder.wrapList(model.getItems());
            itemList.setItems(items);
            createButton.setOnAction(event -> c.call("add"));
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
