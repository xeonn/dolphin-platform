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

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.javafx.DolphinPlatformApplication;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToDoClient extends DolphinPlatformApplication {

    @Override
    protected String getServerEndpoint() {
        return "http://localhost:8080/todo-app/dolphin";
    }

    @Override
    protected void start(Stage primaryStage, ClientContext clientContext) throws Exception {
        ToDoViewBinder viewController = new ToDoViewBinder(clientContext);
        primaryStage.setScene(new Scene(viewController.getParent()));
        primaryStage.show();
    }

    @Override
    protected void onInitializationError(Stage primaryStage, ClientInitializationException initializationException) throws Exception {
        initializationException.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Client initialization error");
        alert.setContentText("An unexpected error has occurred");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        initializationException.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
        System.exit(-1);
    }

    public static void main(String[] args) {
        Logger OD_LOGGER = Logger.getLogger("org.opendolphin");
        OD_LOGGER.setLevel(Level.SEVERE);

        Application.launch(args);
    }

}
