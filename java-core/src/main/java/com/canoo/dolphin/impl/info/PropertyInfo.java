package com.canoo.dolphin.impl.info;

import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.ReflectionHelper;
import org.opendolphin.core.Attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.canoo.dolphin.impl.ClassRepository.FieldType.BASIC_TYPE;
import static com.canoo.dolphin.impl.ClassRepository.FieldType.DOLPHIN_BEAN;
import static com.canoo.dolphin.impl.ClassRepository.FieldType.UNKNOWN;

public abstract class PropertyInfo implements PropertyChangeListener {

    private final Attribute attribute;
    private final String attributeName;

    private Converters.Converter converter;

    public PropertyInfo(Attribute attribute, String attributeName, Converters.Converter converter) {
        this.attribute = attribute;
        this.attributeName = attributeName;
        this.converter = converter;

        this.attribute.addPropertyChangeListener(this);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public abstract Object getPrivileged(Object bean);

    public abstract void setPriviliged(Object bean, Object value);

    public Object convertFromDolphin(Object value) {
        return converter.convertFromDolphin(value);
    }

    public Object convertToDolphin(Object value) {
        if (converter.getFieldType() == UNKNOWN && value != null) {
            final ClassRepository.FieldType fieldType = ReflectionHelper.isBasicType(value.getClass()) ? BASIC_TYPE : DOLPHIN_BEAN;
            updateFieldType(fieldType);
        }
        return converter.convertToDolphin(value);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final ClassRepository.FieldType fieldType = DolphinUtils.mapFieldTypeFromDolphin(event.getNewValue());
        updateFieldType(fieldType);
    }

    protected void updateFieldType(ClassRepository.FieldType fieldType) {
        if (fieldType != UNKNOWN) {
            converter = converter.updateConverter(fieldType);
            attribute.setValue(DolphinUtils.mapFieldTypeToDolphin(fieldType));
            attribute.removePropertyChangeListener(Attribute.VALUE, this);
        }
    }
}
