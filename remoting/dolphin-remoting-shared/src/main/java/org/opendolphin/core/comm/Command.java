package org.opendolphin.core.comm;

import org.codehaus.groovy.runtime.StringGroovyMethods;

import java.io.Serializable;

/**
 * Commands come in two flavors: *Command (active voice) and *Notification (passive voice).
 * A *Command instructs the other side to do something.
 * A *Notification informs the other side that something has happened.
 * Typically, the server sends commands to the client,
 * the client sends notifications to the server with the notable exception of NamedCommand.
 * Neither commands nor notifications contain any logic themselves.
 * They are only "DTOs" that are sent over the wire.
 * The receiving side is responsible for finding the appropriate action.
 */
public class Command implements Serializable {
    public Command() {

    }

    public String getId() {
        return idFor(this.getClass());
    }

    public static String idFor(Class commandClass) {
        return StringGroovyMethods.minus(StringGroovyMethods.minus(commandClass.getSimpleName(), "Command"), "Notification");
    }

    public String toString() {
        return "Command: " + getId();
    }

}
