package com.canoo.dolphin.server.container;

import javax.servlet.ServletContext;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ContainerManager {

    <T> T createManagedController(Class<T> controllerClass);

    void destroyController(Object instance);

}
