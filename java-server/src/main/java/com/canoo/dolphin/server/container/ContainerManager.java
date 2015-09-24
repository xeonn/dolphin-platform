package com.canoo.dolphin.server.container;

import javax.servlet.ServletContext;
import java.util.function.Consumer;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface ContainerManager {

    <T> T createManagedController(Class<T> controllerClass, ModelInjector modelInjector);

    void destroyController(Object instance);

}
