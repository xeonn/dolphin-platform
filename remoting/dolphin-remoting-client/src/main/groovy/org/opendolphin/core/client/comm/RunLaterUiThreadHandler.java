package org.opendolphin.core.client.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunLaterUiThreadHandler implements UiThreadHandler {
    @Override
    public void executeInsideUiThread(final Runnable runnable) {
        executorService.execute(runnable);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
}
