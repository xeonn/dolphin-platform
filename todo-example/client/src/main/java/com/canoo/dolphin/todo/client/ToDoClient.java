/**
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
package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.javafx.JavaFXConfiguration;
import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientContextFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class ToDoClient extends Application {

    private ClientContext clientContext;

    private ToDoViewBinder viewController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        CompletableFuture<ClientContext> connectionPromise = ClientContextFactory.connect(new JavaFXConfiguration("http://localhost:8080/dolphin"));
        connectionPromise.thenAccept(context -> {
            viewController = new ToDoViewBinder(context);
            primaryStage.setScene(new Scene(viewController.getRoot()));
            primaryStage.show();
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(viewController != null) {
            viewController.destroy().get();
        }
        if(clientContext != null) {
            clientContext.disconnect().get();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
