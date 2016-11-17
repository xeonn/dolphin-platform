package org.opendolphin.core.comm;

public class DeletedAllPresentationModelsOfTypeNotification extends Command {

    private String pmType;

    public DeletedAllPresentationModelsOfTypeNotification(String pmType) {
        this.pmType = pmType;
    }

    public DeletedAllPresentationModelsOfTypeNotification() {
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String toString() {
        return super.toString() + " pmType " + pmType;
    }

}
