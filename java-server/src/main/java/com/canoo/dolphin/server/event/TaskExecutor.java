package com.canoo.dolphin.server.event;

import java.io.Serializable;

/**
 * Created by hendrikebbers on 15.09.15.
 */
public interface TaskExecutor extends Serializable {

    public <T> void execute(Class<T> controllerClass, ControllerTask<T> task);

    public <T> void executeForSession(Class<T> controllerClass, ControllerTask<T> task);

}
