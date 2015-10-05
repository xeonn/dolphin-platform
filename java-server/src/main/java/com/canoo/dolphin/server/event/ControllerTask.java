package com.canoo.dolphin.server.event;

/**
 * A functional interface that is used to call a specific task for a controller by using the
 * {@link com.canoo.dolphin.server.event.TaskExecutor}. Since the server part of Dolphin Platform depends on Java 7 we
 * can't use the Java 8 Consumer interface here.
 * @param <T> Controller Type
 */
public interface ControllerTask<T> {

    /**
     * The specified action will be called for the given controller
     * @param controller the controller
     */
    void run(T controller);
}
