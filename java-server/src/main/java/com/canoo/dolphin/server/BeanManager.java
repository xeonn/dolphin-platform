package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.PropertyImpl;
import com.canoo.dolphin.server.impl.DolphinClassRepository;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanManager {

    private final ServerDolphin dolphin;

    private final Map<Object, String> presentationModelToIdMapping = new HashMap<>();
    private final Map<String, Object> idToPresentationModelMapping = new HashMap<>();

    private final DolphinClassRepository classRepository;

    private final PropertyImpl.DolphinConverter dolphinConverter = new PropertyImpl.DolphinConverter() {
        @Override
        public Dolphin getDolphin() {
            return dolphin;
        }

        @Override
        public Object convertToDolphinAttributeValue(Class<?> type, Object object) {
            return object == null || DolphinUtils.isBasicType(type)? object : presentationModelToIdMapping.get(object);
        }

        @Override
        public Object convertToPresentationModelProperty(Class<?> type, Object value) {
            return value == null || DolphinUtils.isBasicType(type)? value : idToPresentationModelMapping.get(value);
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

            String modelType = beanClass.getSimpleName();
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
                    Property property = new PropertyImpl(dolphinConverter, attribute.getId(), field.getType());
                    DolphinUtils.setPrivileged(field, instance, property);
                }
            }

            presentationModelToIdMapping.put(instance, model.getId());
            idToPresentationModelMapping.put(model.getId(), instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    public <T> void delete(T bean) {
        String id = presentationModelToIdMapping.get(bean);
        dolphin.remove(dolphin.findPresentationModelById(id));
    }
}
