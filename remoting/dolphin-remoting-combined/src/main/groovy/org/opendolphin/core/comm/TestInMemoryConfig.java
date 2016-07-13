package org.opendolphin.core.comm;

import org.opendolphin.core.client.comm.RunLaterUiThreadHandler;

import java.util.concurrent.CountDownLatch;

public class TestInMemoryConfig extends DefaultInMemoryConfig {

    public TestInMemoryConfig() {
        getServerDolphin().registerDefaultActions();
        getClientConnector().setSleepMillis(0);
        getClientConnector().setUiThreadHandler(new RunLaterUiThreadHandler());
    }

    public void assertionsDone() {
        done.countDown();
    }

    /**
     * for testing purposes, we may want to send commands synchronously such that we better know when to run asserts
     */
    public void sendSynchronously(String commandName) {
        getClientDolphin().send(commandName);
        syncPoint(1);
    }

    /**
     * make sure we continue only after all previous commands have been executed
     */
    public void syncPoint(final int soManyRoundTrips) {
        if (soManyRoundTrips < 1) {
            return;

        }

        final CountDownLatch latch = new CountDownLatch(1);
        getClientDolphin().sync(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                syncPoint((int) soManyRoundTrips - 1);
            }

        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * needed since tests should run fully asynchronous but we have to wait at the end of the test
     */
    private CountDownLatch done = new CountDownLatch(1);
}
