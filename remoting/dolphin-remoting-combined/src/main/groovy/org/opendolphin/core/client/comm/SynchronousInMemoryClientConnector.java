package org.opendolphin.core.client.comm;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An in-memory client connector without any asynchronous calls such that
 * technologies like GWT, Vaadin, or ULC can use it safely on the server side
 * without leaving the request thread.
 * It may also be useful for unit-testing purposes.
 */
public class SynchronousInMemoryClientConnector extends InMemoryClientConnector {
    public SynchronousInMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector) {
        super(clientDolphin, serverConnector);
    }

    public SynchronousInMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector, ICommandBatcher commandBatcher) {
        super(clientDolphin, serverConnector, commandBatcher);
    }

    protected void startCommandProcessing() {
        /* do nothing! */
    }

    public void send(Command command, OnFinishedHandler callback) {
        List<Command> answer = transmit(new ArrayList(Arrays.asList(command)));
        CommandAndHandler handler = new CommandAndHandler();
        handler.setCommand(command);
        handler.setHandler(callback);
        processResults(answer, new ArrayList(Arrays.asList(handler)));
    }

    public void send(Command command) {
        send(command, null);
    }

}
