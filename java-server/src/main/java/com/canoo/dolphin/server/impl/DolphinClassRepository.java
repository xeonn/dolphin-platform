package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.mapping.DolphinProperty;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.mapping.impl.DolphinUtils;
import com.canoo.dolphin.server.PresentationModelBuilder;
import com.canoo.dolphin.server.util.DolphinServerUtils;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hendrikebbers on 26.03.15.
 */
public class DolphinClassRepository {

    private ServerDolphin dolphin;

    private Set<Class<?>> registered;

    private static final String PM_TYPE = DolphinClassRepository.class.getSimpleName();

    public DolphinClassRepository(ServerDolphin dolphin) {
        this.dolphin = dolphin;
        registered = new HashSet<>();
    }

    public void register(Class<?> beanClass) {
        if (registered.contains(beanClass)) {
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
                builder.withAttribute(attributeName, field.getType().getSimpleName());
            }
        }
    }

}
