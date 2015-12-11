package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.AbstractControllerActionCallBean;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.internal.BeanRepository;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

public class ServerControllerActionCallBean extends AbstractControllerActionCallBean {

    private final BeanRepository beanRepository;
    private final PresentationModel pm;

    public ServerControllerActionCallBean(BeanRepository beanRepository, PresentationModel pm) {
        this.beanRepository = beanRepository;
        this.pm = pm;
    }

    public String getControllerId() {
        return (String) pm.findAttributeByPropertyName(CONTROLLER_ID).getValue();
    }

    public String getActionName() {
        return (String) pm.findAttributeByPropertyName(ACTION_NAME).getValue();
    }

    public void setError(boolean error) {
        pm.findAttributeByPropertyName(ERROR_CODE).setValue(error);
    }

    public Object getParam(String name) {
        final String internalName = PARAM_PREFIX + name;
        final Attribute valueAttribute = pm.findAttributeByPropertyNameAndTag(internalName, Tag.VALUE);
        final Attribute typeAttribute = pm.findAttributeByPropertyNameAndTag(internalName, Tag.VALUE_TYPE);
        if (valueAttribute == null || typeAttribute == null) {
            throw new IllegalArgumentException(String.format("Invoking DolphinAction requires parameter '%s', but it was not send", name));
        }
        final ClassRepositoryImpl.FieldType fieldType = DolphinUtils.mapFieldTypeFromDolphin(typeAttribute.getValue());
        return beanRepository.mapDolphinToObject(valueAttribute.getValue(), fieldType);
    }
}
