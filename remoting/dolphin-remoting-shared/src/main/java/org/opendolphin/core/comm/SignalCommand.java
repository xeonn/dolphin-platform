package org.opendolphin.core.comm;

/**
 * A command where the id can be set from the outside for general purposes.
 * Signal commands are transmitted outside the usual sequence but possibly in the same
 * session. Therefore any handler for this command must neither change nor access any unprotected shared
 * mutable state like the dolphin instance or the model store.
 */
public class SignalCommand extends Command {
    public SignalCommand() {
    }

    public SignalCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
