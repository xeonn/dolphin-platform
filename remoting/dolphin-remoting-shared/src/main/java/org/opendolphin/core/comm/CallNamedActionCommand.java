package org.opendolphin.core.comm;

public class CallNamedActionCommand extends Command {
    public CallNamedActionCommand() {
    }

    public CallNamedActionCommand(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String toString() {
        return super.toString() + " actionName: " + actionName;
    }

    private String actionName;
}
