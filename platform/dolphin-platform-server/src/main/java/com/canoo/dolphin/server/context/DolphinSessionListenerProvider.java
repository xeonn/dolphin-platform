package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.DolphinSessionListener;
import com.canoo.dolphin.server.bootstrap.DolphinPlatformBoostrapException;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hendrikebbers on 31.05.16.
 */
public class DolphinSessionListenerProvider {

    private final ContainerManager containerManager;

    private List<DolphinSessionListener> contextListeners;

    private final ClasspathScanner classpathScanner;

    public DolphinSessionListenerProvider(final ContainerManager containerManager, ClasspathScanner classpathScanner) {
        this.containerManager = Assert.requireNonNull(containerManager, "containerManager");
        this.classpathScanner = Assert.requireNonNull(classpathScanner, "classpathScanner");
    }

    public synchronized List<DolphinSessionListener> getAllListeners() {
        if(contextListeners == null) {
            contextListeners = new ArrayList<>();
            Set<Class<?>> listeners = classpathScanner.getTypesAnnotatedWith(DolphinListener.class);
            for (Class<?> listenerClass : listeners) {
                try {
                    if (DolphinSessionListener.class.isAssignableFrom(listenerClass)) {
                        DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                        contextListeners.add(listener);
                    }
                } catch (Exception e) {
                    throw new DolphinPlatformBoostrapException("Error in creating DolphinSessionListener " + listenerClass, e);
                }
            }
        }
        return contextListeners;
    }

}
