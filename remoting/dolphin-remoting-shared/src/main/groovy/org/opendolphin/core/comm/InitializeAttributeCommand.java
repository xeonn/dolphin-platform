package org.opendolphin.core.comm;

import org.opendolphin.core.Tag;

public class InitializeAttributeCommand extends Command {

    private String pmId;

    private String propertyName;

    private String qualifier;

    private Object newValue;

    private String pmType;

    private Tag tag = Tag.VALUE;

    public InitializeAttributeCommand() {
    }

    public InitializeAttributeCommand(String pmId, String propertyName, String qualifier, Object newValue) {
        this.pmId = pmId;
        this.propertyName = propertyName;
        this.qualifier = qualifier;
        this.newValue = newValue;
    }

    public InitializeAttributeCommand(String pmId, String propertyName, String qualifier, Object newValue, String pmType) {
        this.pmId = pmId;
        this.propertyName = propertyName;
        this.qualifier = qualifier;
        this.newValue = newValue;
        this.pmType = pmType;
    }

    public InitializeAttributeCommand(String pmId, String propertyName, String qualifier, Object newValue, String pmType, Tag tag) {
        this.pmId = pmId;
        this.propertyName = propertyName;
        this.qualifier = qualifier;
        this.newValue = newValue;
        this.pmType = pmType;
        this.tag = tag;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String toString() {
        return super.toString() + " pm \'" + pmId + "\' pmType\'" + pmType + "\' property \'" + propertyName + "\' initial value \'" + String.valueOf(newValue) + "\' qualifier " + qualifier + " tag " + String.valueOf(tag);
    }
}
