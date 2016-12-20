package org.opendolphin.core.comm;

/**
 * A command where the id can be set from the outside for general purposes.
 */
public class NamedCommand extends Command {
    public NamedCommand() {
    }

    public NamedCommand(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private String id;
}
