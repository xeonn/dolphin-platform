package org.opendolphin.core.comm;

public class PresentationModelResetedCommand extends Command {

    private String pmId;

    public PresentationModelResetedCommand() {
    }

    public PresentationModelResetedCommand(String pmId) {
        this.pmId = pmId;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String toString() {
        return super.toString() + " pmId " + pmId;
    }
}
