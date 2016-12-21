package org.opendolphin.core.server.action;

import org.opendolphin.core.comm.EmptyNotification;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;
import java.util.logging.Logger;

/**
 * An action that does nothing on the server.
 * It is only used to hook into the communication at a known point
 * such that the onFinished handler for the command is executed
 * in the expected sequence.
 */
public class EmptyAction implements ServerAction {

    private static final Logger LOG = Logger.getLogger(EmptyAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(EmptyNotification.class, new CommandHandler<EmptyNotification>() {
            @Override
            public void handleCommand(EmptyNotification command, List response) {
                LOG.finest("empty action reached - doing nothing on the server");
            }
        });
    }

}
