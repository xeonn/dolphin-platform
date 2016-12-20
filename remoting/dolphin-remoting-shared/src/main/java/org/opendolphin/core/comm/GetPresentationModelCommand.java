package org.opendolphin.core.comm;

public class GetPresentationModelCommand extends Command {
    public GetPresentationModelCommand() {
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    @Override
    public String toString() {
        return super.toString() + " for presentation model for id " + pmId;
    }

    private String pmId;
}
