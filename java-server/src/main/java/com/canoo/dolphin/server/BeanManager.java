package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.DolphinClassRepository;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.PropertyImpl;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanManager {

    private final ServerDolphin dolphin;

    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();
    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();

    private final DolphinClassRepository classRepository;

    private final PropertyImpl.DolphinAccessor dolphinAccessor = new PropertyImpl.DolphinAccessor() {
        @Override
        public Object getValue(Attribute attribute) {
            final Object attributeValue = attribute.getValue();
            switch (classRepository.getFieldType(attribute)) {
                case ENUM:
                    // TODO Implement enums
                    return null;
                case DOLPHIN_BEAN:
                    return dolphinIdToObjectPm.get(attributeValue);
            }
            return attributeValue;
        }

        @Override
        public void setValue(Attribute attribute, Object value) {
            final DolphinClassRepository.FieldType fieldType = classRepository.setFieldTypeFromValue(attribute, value);
            final Object attributeValue;
            switch (fieldType) {
                case ENUM:
                    // TODO Implement enums
                    return;
                case DOLPHIN_BEAN:
                    attributeValue = objectPmToDolphinPm.get(value).getId();
                    break;
                default:
                    attributeValue = value;
            }
            attribute.setValue(attributeValue);
        }
    };

    public BeanManager(ServerDolphin dolphin) {
        this.dolphin = dolphin;
        classRepository = new DolphinClassRepository(dolphin);
    }

    public <T> T create(Class<T> beanClass) {
        try {
            classRepository.register(beanClass);

            T instance = beanClass.newInstance();

            PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

            String modelType = beanClass.getName();
            DolphinBean beanAnnotation = beanClass.getAnnotation(DolphinBean.class);
            if (beanAnnotation != null && !beanAnnotation.value().isEmpty()) {
                modelType = beanAnnotation.value();
            }
            builder.withType(modelType);

            for (Field field : DolphinUtils.getInheritedDeclaredFields(beanClass)) {
                if (Property.class.isAssignableFrom(field.getType())) {

                    String attributeName = field.getName();
                    DolphinProperty propertyAnnotation = field.getAnnotation(DolphinProperty.class);
                    if (propertyAnnotation != null && !propertyAnnotation.value().isEmpty()) {
                        attributeName = propertyAnnotation.value();
                    }
                    builder.withAttribute(attributeName);
                }
            }

            PresentationModel model = builder.create();

            for (Field field : DolphinUtils.getInheritedDeclaredFields(beanClass)) {
                if (Property.class.isAssignableFrom(field.getType())) {
                    String attributeName = field.getName();
                    DolphinProperty propertyAnnotation = field.getAnnotation(DolphinProperty.class);
                    if (propertyAnnotation != null && !propertyAnnotation.value().isEmpty()) {
                        attributeName = propertyAnnotation.value();
                    }
                    Attribute attribute = model.findAttributeByPropertyName(attributeName);
                    @SuppressWarnings("unchecked")
                    Property property = new PropertyImpl(dolphinAccessor, attribute);
                    DolphinUtils.setPrivileged(field, instance, property);
                }
            }

            objectPmToDolphinPm.put(instance, model);
            dolphinIdToObjectPm.put(model.getId(), instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    public <T> void delete(T bean) {
        final PresentationModel model = objectPmToDolphinPm.remove(bean);
        if (model != null) {
            dolphinIdToObjectPm.remove(model.getId());
            dolphin.remove(model);
        }
    }
}
