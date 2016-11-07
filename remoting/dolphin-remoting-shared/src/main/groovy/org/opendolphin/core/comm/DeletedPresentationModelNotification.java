package org.opendolphin.core.comm;

public class DeletedPresentationModelNotification extends Command {
    public DeletedPresentationModelNotification(String pmId) {
        this.pmId = pmId;
    }

    public DeletedPresentationModelNotification() {
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
