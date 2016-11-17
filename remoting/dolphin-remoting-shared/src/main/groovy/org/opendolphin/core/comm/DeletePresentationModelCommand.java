package org.opendolphin.core.comm;

public class DeletePresentationModelCommand extends Command {
    public DeletePresentationModelCommand() {
    }

    public DeletePresentationModelCommand(String pmId) {
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

    private String pmId;
}
