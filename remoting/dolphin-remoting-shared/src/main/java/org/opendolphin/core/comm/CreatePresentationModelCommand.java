package org.opendolphin.core.comm;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePresentationModelCommand extends Command {

    private String pmId;

    private String pmType;

    private boolean clientSideOnly = false;

    private List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();

    public CreatePresentationModelCommand(String pmId, String pmType, List<Map<String, Object>> attributes, boolean clientSideOnly) {
        this.pmId = pmId;
        this.pmType = pmType;
        this.clientSideOnly = clientSideOnly;
        this.attributes = attributes;
    }

    public CreatePresentationModelCommand() {
    }

    /**
     * @deprecated use ServerFacade convenience methods (it is ok to use it from the client atm)
     */
    public static <T extends Attribute> CreatePresentationModelCommand makeFrom(PresentationModel<T> model) {
        CreatePresentationModelCommand result = new CreatePresentationModelCommand();
        result.setPmId(model.getId());
        result.setPmType(model.getPresentationModelType());
        for (T attr : model.getAttributes()) {

            Map attributeMap = new HashMap();
            attributeMap.put("propertyName", attr.getPropertyName());
            attributeMap.put("id", attr.getId());
            attributeMap.put("qualifier", attr.getQualifier());
            attributeMap.put("value", attr.getValue());
            attributeMap.put("baseValue", attr.getBaseValue());
            if (attr.getTag() != null) {
                attributeMap.put("tag", attr.getTag().getName());
            } else {
                attributeMap.put("tag", null);
            }

            result.getAttributes().add(attributeMap);
        }

        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " pmId " + pmId + " pmType " + pmType + (clientSideOnly ? "CLIENT-SIDE-ONLY!" : "") + " attributes " + attributes;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public boolean getClientSideOnly() {
        return clientSideOnly;
    }

    public void setClientSideOnly(boolean clientSideOnly) {
        this.clientSideOnly = clientSideOnly;
    }

    public List<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Map<String, Object>> attributes) {
        this.attributes = attributes;
    }
}
