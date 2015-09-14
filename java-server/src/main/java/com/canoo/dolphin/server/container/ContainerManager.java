package com.canoo.dolphin.server.container;

import javax.servlet.ServletContext;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ContainerManager {

    <T> T createManagedController(ServletContext sc, Class<T> controllerClass);

    void destroyController(ServletContext sc, Object instance);

}
