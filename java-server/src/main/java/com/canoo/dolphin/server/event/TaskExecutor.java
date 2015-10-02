package com.canoo.dolphin.server.event;

import java.io.Serializable;

/**
 * <p>
 * The task executor can be used to trigger specific dolphin controllers (see {@link com.canoo.dolphin.server.DolphinController}).
 * This API is defined in addition to the event bus (see {@link com.canoo.dolphin.server.event.DolphinEventBus}) to handle
 * the usage of reuseable and external components.
 *</p>
 * <p>
 * Example:
 * Let's say we have create a reuseable MVC group to show notifications. This MVG group should be reused in several projects.
 * In this case using the event bus don't make much sense since if the MVC group is an external / private module we
 * maybe don't know what is the event topic that is used by the MVC widget. In addition if we have several of these widgets
 * we don't know if 2 of them use the same topic. Therefore it's better to interact directly with the MVC widget. To do so
 * can use the {@see TaskExecutor} and simply call public methods of the widget controller.
 * <br>
 * <center><img src="doc-files/task-executor.png" alt="Notification MVC Widget example"></center>
 *</p>
 *<p>
 * The {@see TaskExecutor} provides the possibility to call methods for all given controller instances of the given type
 * in the current session or on the whole server. This can be used to create team application in that the same data can
 * easily be shared and edited between several clients in realtime.
 *</p>
 */
public interface TaskExecutor extends Serializable {

    public <T> void execute(Class<T> controllerClass, ControllerTask<T> task);

    public <T> void executeForSession(Class<T> controllerClass, ControllerTask<T> task);

}
