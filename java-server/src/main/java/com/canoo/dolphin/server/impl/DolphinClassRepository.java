package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.server.PresentationModelBuilder;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DolphinClassRepository {

    public static enum FieldType { UNKNOWN, BASIC_TYPE, ENUM, DOLPHIN_BEAN }

    private ServerDolphin dolphin;

    private Map<String, PresentationModel> models = new HashMap<>();

    private static final String PM_TYPE = DolphinClassRepository.class.getName();

    public DolphinClassRepository(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    public void register(Class<?> beanClass) {
        String id = beanClass.getName();
        DolphinBean beanAnnotation = beanClass.getAnnotation(DolphinBean.class);
        if (beanAnnotation != null && !beanAnnotation.value().isEmpty()) {
            id = beanAnnotation.value();
        }

        if (models.containsKey(id)) {
            return;
        }
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        builder.withType(PM_TYPE).withId(beanClass.getName());

        if (beanClass.isEnum()) {
            final Object[] values = beanClass.getEnumConstants();
            for (int i = 0, n = values.length; i < n; i++) {
                builder.withAttribute(Integer.toString(i), values[i].toString());
            }
        } else {
            DolphinUtils.forAllProperties(beanClass, new DolphinUtils.PropertyIterator() {
                @Override
                public void run(Field field, String attributeName) {
                    builder.withAttribute(attributeName, FieldType.UNKNOWN.ordinal(), Tag.VALUE_TYPE);
                    builder.withAttribute(attributeName, null, Tag.VALUE);
                }
            });
        }

        final PresentationModel presentationModel = builder.create();
        models.put(id, presentationModel);
    }

    private Attribute findClassAttribute(Attribute fieldAttribute, Tag tag) {
        final String beanClass = fieldAttribute.getPresentationModel().getPresentationModelType();
        final PresentationModel classModel = models.get(beanClass);
        return classModel.findAttributeByPropertyNameAndTag(fieldAttribute.getPropertyName(), tag);
    }

    public FieldType getFieldType(Attribute fieldAttribute) {
        final Attribute classAttribute = findClassAttribute(fieldAttribute, Tag.VALUE_TYPE);
        try {
            return FieldType.values()[(Integer)classAttribute.getValue()];
        } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
            // do nothing
        }
        return FieldType.UNKNOWN;
    }

    public Class<?> getFieldClass(Attribute fieldAttribute) {
        final Attribute classAttribute = findClassAttribute(fieldAttribute, Tag.VALUE);
        try {
            return Class.forName((String) classAttribute.getValue());
        } catch (ClassCastException | ClassNotFoundException ex) {
            // do nothing
        }
        return null;
    }

    public FieldType setFieldTypeFromValue(Attribute fieldAttribute, Object value) {
        if (value == null) {
            return FieldType.UNKNOWN;
        }
        final FieldType fieldType = calculateFieldType(value.getClass());
        if (fieldType != FieldType.UNKNOWN) {
            findClassAttribute(fieldAttribute, Tag.VALUE_TYPE).setValue(fieldType.ordinal());
            findClassAttribute(fieldAttribute, Tag.VALUE).setValue(value.getClass().getName());
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
