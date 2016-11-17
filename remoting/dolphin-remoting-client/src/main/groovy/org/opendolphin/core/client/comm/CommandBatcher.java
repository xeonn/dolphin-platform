package org.opendolphin.core.client.comm;

import java.util.Collections;
import java.util.List;

public class CommandBatcher implements ICommandBatcher {

    public CommandBatcher() {
        this.waitingBatches = new CommandBatcherQueue();
    }

    public void batch(CommandAndHandler commandAndHandler) {
        waitingBatches.add(Collections.singletonList(commandAndHandler));
    }

    public boolean isEmpty() {
        return waitingBatches.length() == 0;
    }

    public DataflowQueue<List<CommandAndHandler>> getWaitingBatches() {
        return waitingBatches;
    }

    private final DataflowQueue<List<CommandAndHandler>> waitingBatches;
}
