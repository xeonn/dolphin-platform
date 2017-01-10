package com.canoo.dolphin.server;

import java.util.concurrent.Future;

/**
 * The BackgroundRunner can be used to execute tasks later on a specific client session (see {@link DolphinSession}).
 */
public interface BackgroundRunner {

    /**
     * Executes the given task later in the given client session
     * @param clientSessionId id of the client session
     * @param task the task
     * @return a future that is finished once the task is finished.
     */
    Future<Void> runLaterInClientSession(String clientSessionId, Runnable task);

}
