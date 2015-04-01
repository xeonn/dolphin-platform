package com.canoo.dolphin.example.client;

import com.canoo.dolphin.client.ModelCreationListener;
import com.canoo.dolphin.client.ModelManager;
import com.canoo.dolphin.example.HelloWorldModel;

import javax.swing.*;
import java.awt.*;

public class HelloWorldClient extends JFrame {

    public HelloWorldClient() throws HeadlessException {
        final JLabel label = new JLabel("TADA");

        ModelManager manager = null;
        manager.addModelCreationListener(HelloWorldModel.class, new ModelCreationListener<HelloWorldModel>() {
            @Override
            public void modelCreated(HelloWorldModel model) {
                //TODO: Bind label text to model.getTextProperty()
            }
        });
        manager.callAction("hello-world:init");

        getContentPane().add(label);
        setSize(800, 600);
    }

    public static void main(String... args) {
        new HelloWorldClient().setVisible(true);
    }
}
