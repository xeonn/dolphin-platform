package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

public abstract class AbstractFXMLViewBinder<M> extends AbstractViewBinder<M> {

    private Node rootNode;

    public AbstractFXMLViewBinder(ClientContext clientContext, String controllerName, URL fxmlLocation) throws IOException {
        super(clientContext, controllerName);
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        loader.setController(this);
        rootNode = loader.load();
    }

    public Node getRootNode() {
        return rootNode;
    }
}
