package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DolphinContextLifecycle {

    private ContainerManager containerManager;

    public DolphinContextLifecycle(ContainerManager containerManager) {
        Assert.requireNonNull(containerManager, "containerManager");
        this.containerManager = containerManager;
    }

    public void onCreate() {
        for(DolphinContextListener contextListener : getContextListeners()) {
            try {
                contextListener.contextCreated();
            } catch(Exception e) {
                //TODO????
            }
        }
    }

    public void onDestroy() {
        for(DolphinContextListener contextListener : getContextListeners()) {
            try {
                contextListener.contextDestroyed();
            } catch(Exception e) {
                //TODO????
            }
        }
    }

    public List<DolphinContextListener> getContextListeners() {
        Set<Class<?>> foundClasses = ClasspathScanner.getInstance().getTypesAnnotatedWith(DolphinListener.class);
        List<DolphinContextListener> listeners = new ArrayList<>();
        for(Class<?> cls : foundClasses) {
            if(DolphinContextListener.class.isAssignableFrom(cls) && !cls.equals(DolphinContextListener.class)) {
                listeners.add(containerManager.createContextListener((Class<? extends DolphinContextListener>) cls));
            }
        }
        return listeners;
    }
}
