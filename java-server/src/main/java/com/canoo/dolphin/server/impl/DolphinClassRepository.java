package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.PresentationModelBuilder;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DolphinClassRepository {

    public static enum FieldType { UNKNOWN, BASIC_TYPE, ENUM, DOLPHIN_BEAN }

    private ServerDolphin dolphin;

    private Map<String, PresentationModel> models = new HashMap<>();

    private static final String PM_TYPE = DolphinClassRepository.class.getSimpleName();

    public DolphinClassRepository(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    public void register(Class<?> beanClass) {
        final String id = beanClass.getName();
        if (models.containsKey(id)) {
            return;
        }
        PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        builder.withType(PM_TYPE).withAttribute("CLASS_NAME", beanClass.getSimpleName());
        for (Field field : DolphinUtils.getInheritedDeclaredFields(beanClass)) {
            if (Property.class.isAssignableFrom(field.getType())) {
                String attributeName = field.getName();
                DolphinProperty propertyAnnotation = field.getAnnotation(DolphinProperty.class);
                if (propertyAnnotation != null && !propertyAnnotation.value().isEmpty()) {
                    attributeName = propertyAnnotation.value();
                }
                builder.withAttribute(attributeName, FieldType.UNKNOWN.ordinal());
            }
        }

        final PresentationModel presentationModel = builder.create();
        models.put(id, presentationModel);
    }

    private Attribute findClassAttribute(Attribute fieldAttribute) {
        final String beanClass = fieldAttribute.getPresentationModel().getPresentationModelType();
        final PresentationModel classModel = models.get(beanClass);
        return classModel.findAttributeByPropertyName(fieldAttribute.getPropertyName());
    }

    public FieldType getFieldType(Attribute fieldAttribute) {
        final Attribute classAttribute = findClassAttribute(fieldAttribute);
        return FieldType.values()[(Integer)classAttribute.getValue()];
    }

    public FieldType setFieldTypeFromValue(Attribute fieldAttribute, Object value) {
        if (value == null) {
            return FieldType.UNKNOWN;
        }
        final FieldType fieldType = calculateFieldType(value.getClass());
        if (fieldType != FieldType.UNKNOWN) {
            final Attribute classAttribute = findClassAttribute(fieldAttribute);
            classAttribute.setValue(fieldType.ordinal());
        }
        return fieldType;
    }

    private static FieldType calculateFieldType(Class<?> clazz) {
        if (clazz == null) {
            return FieldType.UNKNOWN;
        }
        if (DolphinUtils.isBasicType(clazz)) {
            return FieldType.BASIC_TYPE;
        }
        if (clazz.isEnum()) {
            return FieldType.ENUM;
        }
        return FieldType.DOLPHIN_BEAN;
    }

}
