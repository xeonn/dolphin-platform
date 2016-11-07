package org.opendolphin.core.client.comm;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InMemoryClientConnector extends AbstractClientConnector {
    public InMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector) {
        super(clientDolphin);
        this.serverConnector = serverConnector;
    }

    public InMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector, ICommandBatcher commandBatcher) {
        super(clientDolphin, commandBatcher);
        this.serverConnector = serverConnector;
    }

    @Override
    public List<Command> transmit(List<Command> commands) {
        LOGGER.trace("transmitting {} commands", commands.size());
        if (!DefaultGroovyMethods.asBoolean(serverConnector)) {
            LOGGER.warn("no server connector wired for in-memory connector");
            return Collections.EMPTY_LIST;
        }


        if (sleepMillis > 0) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }


        List<Command> result = new LinkedList<Command>();
        for (Command command : commands) {
            LOGGER.trace("processing {}", command);
            ((LinkedList<Command>) result).addAll(serverConnector.receive(command));// there is no need for encoding since we are in-memory
        }


        return result;
    }

    public long getSleepMillis() {
        return sleepMillis;
    }

    public void setSleepMillis(long sleepMillis) {
        this.sleepMillis = sleepMillis;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryClientConnector.class);
    private final ServerConnector serverConnector;
    private long sleepMillis = 0;
}
