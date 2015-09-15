package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.javafx.JavaFXConfiguration;
import com.canoo.dolphin.client.v2.ClientContext;
import com.canoo.dolphin.client.v2.ClientContextFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class ToDoClient extends Application {

    private ToDoViewController viewController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        CompletableFuture<ClientContext> connectionPromise = ClientContextFactory.connect(new JavaFXConfiguration("http://localhost:8080/todo-app/dolphin"));
        connectionPromise.thenAccept(context -> {
            viewController = new ToDoViewController(context);
            primaryStage.setScene(new Scene(viewController.getRoot()));
            primaryStage.show();
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        viewController.destroy().get();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
