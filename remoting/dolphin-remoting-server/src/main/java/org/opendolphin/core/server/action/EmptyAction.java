package org.opendolphin.core.server.action;

import groovy.lang.Closure;
import org.opendolphin.core.comm.EmptyNotification;
import org.opendolphin.core.server.comm.ActionRegistry;

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
        registry.register(EmptyNotification.class, new Closure<Object>(this, this) {
            public void doCall(EmptyNotification command, Object response) {
                LOG.finest("empty action reached - doing nothing on the server");
            }

        });
    }

}
