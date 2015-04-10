package com.canoo.dolphin.icos.poc.platform;

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

public abstract class AbstractDolphinCommand {

    private ServerDolphin dolphin;

    protected ServerDolphin getDolphin() {
        return dolphin;
    }

    public abstract void action();

    public void setDolphin(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    protected void removePresentationModel(String id) {
        getDolphin().remove(dolphin.getAt(id));
    }

    protected void setValue(String modelId, String attributeName, Object value) {
        setValue(getDolphin().getAt(modelId), attributeName, value);
    }

    protected <T> T getValue(String modelId, String attributeName) {
        return getValue(getDolphin().getAt(modelId), attributeName);
    }

    protected <T> T getValue(PresentationModel model, String attributeName) {
        return (T) model.findAttributeByPropertyName(attributeName).getValue();
    }

    protected void setValue(PresentationModel model, String attributeName, Object value) {
        model.findAttributeByPropertyName(attributeName).setValue(value);
    }

    protected PresentationModelBuilder createPresentationModel() {
        return new PresentationModelBuilder(dolphin);
    }

}
