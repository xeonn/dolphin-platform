package org.opendolphin.core.client.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunLaterUiThreadHandler implements UiThreadHandler {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void executeInsideUiThread(final Runnable runnable) {
        executorService.execute(runnable);
    }

}
