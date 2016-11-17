package org.opendolphin.core.comm;

public class GetPresentationModelCommand extends Command {
    public GetPresentationModelCommand() {
    }

    public GetPresentationModelCommand(String pmId) {
        this.pmId = pmId;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String toString() {
        return super.toString() + " for presentation model for id " + pmId;
    }

    private String pmId;
}
