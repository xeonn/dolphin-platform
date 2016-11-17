package org.opendolphin.core.comm;

public class ResetPresentationModelCommand extends Command {

    private String pmId;

    public ResetPresentationModelCommand() {
    }

    public ResetPresentationModelCommand(String pmId) {
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
