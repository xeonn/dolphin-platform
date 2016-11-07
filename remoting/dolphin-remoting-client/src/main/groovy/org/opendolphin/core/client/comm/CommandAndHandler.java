package org.opendolphin.core.client.comm;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.EmptyNotification;
import org.opendolphin.core.comm.NamedCommand;

public class CommandAndHandler {

    private Command command;

    private OnFinishedHandler handler;

    public CommandAndHandler() {
    }

    public CommandAndHandler(Command command, OnFinishedHandler handler) {
        this.command = command;
        this.handler = handler;
    }

    /**
     * whether this command/handler can be batched
     */
    public boolean isBatchable() {
        if (DefaultGroovyMethods.asBoolean(handler)) return false;
        if (command instanceof NamedCommand) return false;
        if (command instanceof EmptyNotification) return false;
        return true;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public OnFinishedHandler getHandler() {
        return handler;
    }

    public void setHandler(OnFinishedHandler handler) {
        this.handler = handler;
    }

}
