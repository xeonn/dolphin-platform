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
package com.canoo.dolphin.todo.client;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.DolphinRuntimeException;
import com.canoo.dolphin.client.javafx.DolphinPlatformApplication;
import com.canoo.dolphin.client.javafx.JavaFXConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class ToDoClient extends DolphinPlatformApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ToDoClient.class);

    @Override
    protected URL getServerEndpoint() throws MalformedURLException {
        return new URL("http://localhost:8080/todo-app/dolphin");
    }

    @Override
    protected JavaFXConfiguration getClientConfiguration() {
        try {
            JavaFXConfiguration configuration = new JavaFXConfiguration(new URL("http://localhost:8080/todo-app/dolphin"));
            configuration.setGc(true);
            return configuration;
        } catch (MalformedURLException e) {
            throw new ClientInitializationException("Error in creating config", e);
        }
    }

    @Override
    protected void start(Stage primaryStage, ClientContext clientContext) throws Exception {
        ToDoView viewController = new ToDoView(clientContext);
        Scene scene = new Scene(viewController.getParent());
        scene.getStylesheets().add(ToDoClient.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showError(Window parent, String header, String content, Exception e) {
        LOG.error("Dolphin Platform error!", e);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        Platform.exit();
    }

    @Override
    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException) {
        showError(primaryStage, "Error on initialization", "A error happened while initializing the Client and Connection", initializationException);
    }

    @Override
    protected void onRuntimeError(Stage primaryStage, DolphinRuntimeException runtimeException) {
        showError(primaryStage, "Error at runtime", "A error happened at runtime", runtimeException);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
