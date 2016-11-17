package org.opendolphin.core.comm;

public class SwitchPresentationModelCommand extends Command {

    private String pmId;

    private String sourcePmId;

    public SwitchPresentationModelCommand() {
    }

    public SwitchPresentationModelCommand(String pmId, String sourcePmId) {
        this.pmId = pmId;
        this.sourcePmId = sourcePmId;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getSourcePmId() {
        return sourcePmId;
    }

    public void setSourcePmId(String sourcePmId) {
        this.sourcePmId = sourcePmId;
    }

    public String toString() {
        return super.toString() + " " + pmId + " to attributes of  " + sourcePmId;
    }

}
