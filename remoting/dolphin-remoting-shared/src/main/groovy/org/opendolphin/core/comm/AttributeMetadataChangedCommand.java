package org.opendolphin.core.comm;

public class AttributeMetadataChangedCommand extends Command {

    private String attributeId;

    private String metadataName;

    private Object value;

    public AttributeMetadataChangedCommand() {
    }

    public AttributeMetadataChangedCommand(String attributeId, String metadataName, Object value) {
        this.attributeId = attributeId;
        this.metadataName = metadataName;
        this.value = value;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(String metadataName) {
        this.metadataName = metadataName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return super.toString() + " attr:" + attributeId + ", metadataName:" + metadataName + " value:" + String.valueOf(value);
    }
}
