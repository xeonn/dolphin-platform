package org.opendolphin.core.comm;

public class ValueChangedCommand extends Command {

    private String attributeId;

    private Object oldValue;

    private Object newValue;

    public ValueChangedCommand() {
    }

    public ValueChangedCommand(String attributeId, Object oldValue, Object newValue) {
        this.attributeId = attributeId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String toString() {
        return super.toString() + " attr:" + attributeId + ", " + String.valueOf(oldValue) + " -> " + String.valueOf(newValue);
    }

}
