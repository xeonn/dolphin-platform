package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.DolphinBean;
import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.PropertyImpl;
import com.canoo.dolphin.server.impl.DolphinClassRepository;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class BeanManager {

    private ServerDolphin dolphin;

    private Map<Object, String> presentationModelIdMapping;

    private DolphinClassRepository classRepository;

    public BeanManager(ServerDolphin dolphin) {
        this.dolphin = dolphin;
        presentationModelIdMapping = new HashMap<>();
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
                    Property property = new PropertyImpl(dolphin, attribute.getId());
                    DolphinUtils.setPrivileged(field, instance, property);
                }
            }

            presentationModelIdMapping.put(instance, model.getId());
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    public <T> void delete(T bean) {
        String id = presentationModelIdMapping.get(bean);
        dolphin.remove(dolphin.findPresentationModelById(id));
    }


}
