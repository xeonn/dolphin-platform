package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.context.DolphinContextListener;
import com.canoo.dolphin.server.context.DolphinListener;

@DolphinListener
public class DisconnectListener implements DolphinContextListener {

    @Override
    public void contextCreated() {
        System.out.println("Dolphin context created");
    }

    @Override
    public void contextDestroyed() {
        System.out.println("Dolphin context destroyed");
    }
}
