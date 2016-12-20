package org.opendolphin.core.comm;

public class SavedPresentationModelNotification extends Command {
    public SavedPresentationModelNotification() {
    }

    public SavedPresentationModelNotification(String pmId) {
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
