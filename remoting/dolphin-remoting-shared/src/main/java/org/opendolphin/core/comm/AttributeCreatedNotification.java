package org.opendolphin.core.comm;

import org.opendolphin.core.Tag;

public class AttributeCreatedNotification extends Command {

    private String pmId;

    private String attributeId;

    private String propertyName;

    private Object newValue;

    private String qualifier;

    private Tag tag = Tag.VALUE;

    public AttributeCreatedNotification() {
    }

    public AttributeCreatedNotification(String pmId, String attributeId, String propertyName, Object newValue, String qualifier, Tag tag) {
        this.pmId = pmId;
        this.attributeId = attributeId;
        this.propertyName = propertyName;
        this.newValue = newValue;
        this.qualifier = qualifier;
        this.tag = tag;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String toString() {
        return super.toString() + " attr:" + attributeId + ", pm:" + pmId + ", property:" + propertyName + " value:" + String.valueOf(newValue) + " qualifier:" + qualifier + " tag:" + (tag == null ? null : tag.getName());
    }

}
