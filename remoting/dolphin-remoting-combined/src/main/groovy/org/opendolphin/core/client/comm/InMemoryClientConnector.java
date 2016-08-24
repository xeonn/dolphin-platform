/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryClientConnector.class);

    private long sleepMillis = 0;

    private final ServerConnector serverConnector;

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
        LOG.trace("transmitting {} commands", commands.size());
        if (!DefaultGroovyMethods.asBoolean(serverConnector)) {
            LOG.warn("no server connector wired for in-memory connector");
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
            LOG.trace("processing {}", command);
            result.addAll(serverConnector.receive(command));// there is no need for encoding since we are in-memory
        }

        return result;
    }

    public long getSleepMillis() {
        return sleepMillis;
    }

    public void setSleepMillis(long sleepMillis) {
        this.sleepMillis = sleepMillis;
    }
}
