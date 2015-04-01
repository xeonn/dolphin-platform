package com.canoo.dolphin.example.client;

import com.canoo.dolphin.client.ModelCreationListener;
import com.canoo.dolphin.client.ModelManager;
import com.canoo.dolphin.example.HelloWorldModel;

import javax.swing.*;
import java.awt.*;

public class HelloWorldClient extends JFrame {

    public HelloWorldClient() throws HeadlessException {
        final JTextField textfield = new JTextField();

        //TODO: Es muss einfach sein einen Manager im Client zu erstellen.
        ModelManager manager = null;
        manager.addModelCreationListener(HelloWorldModel.class, new ModelCreationListener<HelloWorldModel>() {
            @Override
            public void modelCreated(HelloWorldModel model) {
                //TODO: Bind textfield text to model.getTextProperty()
            }
        });
        manager.callAction("hello-world:init");

        getContentPane().add(textfield);
        setSize(800, 600);
    }

    public static void main(String... args) {
        new HelloWorldClient().setVisible(true);
    }
}
