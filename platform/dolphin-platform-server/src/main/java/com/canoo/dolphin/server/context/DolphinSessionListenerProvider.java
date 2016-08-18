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
