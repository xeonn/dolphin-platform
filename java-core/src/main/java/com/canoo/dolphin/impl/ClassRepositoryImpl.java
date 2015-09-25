package com.canoo.dolphin.impl;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.internal.info.ClassInfo;
import com.canoo.dolphin.impl.info.ClassPropertyInfo;
import com.canoo.dolphin.internal.info.PropertyInfo;
import org.opendolphin.core.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code ClassRepository} manages {@link ClassInfo} objects for all registered Dolphin Beans. A {@code ClassInfo}
 * object keeps information on class level about the properties and ObservableLists of a DolphinBean.
 */
public class ClassRepositoryImpl implements ClassRepository {

    public enum FieldType {UNKNOWN, BASIC_TYPE, DOLPHIN_BEAN}

    private final PresentationModelBuilderFactory builderFactory;
    private final Converters.Converter initialConverter;

    private final Map<Class<?>, ClassInfo> classToClassInfoMap = new HashMap<>();
    private final Map<String, ClassInfo> modelTypeToClassInfoMap = new HashMap<>();

    public ClassRepositoryImpl(Dolphin dolphin, BeanRepository beanRepository, PresentationModelBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
        this.initialConverter = new Converters(beanRepository).getUnknownTypeConverter();

        dolphin.addModelStoreListener(DolphinConstants.DOLPHIN_BEAN, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                try {
                    final String className = (String) event.getPresentationModel().findAttributeByPropertyName(DolphinConstants.JAVA_CLASS).getValue();
                    final Class<?> beanClass = Class.forName(className);
                    final ClassInfo classInfo = createClassInfoForClass(beanClass, event.getPresentationModel());
                    classToClassInfoMap.put(beanClass, classInfo);
                    modelTypeToClassInfoMap.put(classInfo.getModelType(), classInfo);
                } catch (ClassNotFoundException e) {
                    // Ignore unknown classes
                }
            }
        });
    }

    public ClassInfo getClassInfo(String modelType) {
        return modelTypeToClassInfoMap.get(modelType);
    }

    public ClassInfo getOrCreateClassInfo(final Class<?> beanClass) {
        final ClassInfo existingClassInfo = classToClassInfoMap.get(beanClass);
        if (existingClassInfo != null) {
            return existingClassInfo;
        }

        final PresentationModel model = createPresentationModelForClass(beanClass);
        final ClassInfo newClassInfo = createClassInfoForClass(beanClass, model);

        classToClassInfoMap.put(beanClass, newClassInfo);
        return newClassInfo;
    }

    private PresentationModel createPresentationModelForClass(Class<?> beanClass) {
        final String id = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);
        final PresentationModelBuilder builder = builderFactory.createBuilder()
                .withId(id)
                .withType(DolphinConstants.DOLPHIN_BEAN)
                .withAttribute(DolphinConstants.JAVA_CLASS, beanClass.getName());

        for (final Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            if (Property.class.isAssignableFrom(field.getType()) || ObservableList.class.isAssignableFrom(field.getType())) {
                final String attributeName = DolphinUtils.getDolphinAttributePropertyNameForField(field);
                builder.withAttribute(attributeName, FieldType.UNKNOWN.ordinal(), Tag.VALUE);
            }
        }

        return builder.create();
    }

    private ClassInfo createClassInfoForClass(Class<?> beanClass, PresentationModel model) {
        final List<PropertyInfo> propertyInfos = new ArrayList<>();
        final List<PropertyInfo> observableListInfos = new ArrayList<>();


        for (Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            PropertyType type = null;
            if (Property.class.isAssignableFrom(field.getType())) {
                type = PropertyType.PROPERTY;
            } else if (ObservableList.class.isAssignableFrom(field.getType())) {
                type = PropertyType.OBSERVABLE_LIST;
            }
            if (type != null) {
                final String attributeName = DolphinUtils.getDolphinAttributePropertyNameForField(field);
                final Attribute attribute = model.findAttributeByPropertyName(attributeName);
                final PropertyInfo propertyInfo = new ClassPropertyInfo(attribute, attributeName, initialConverter, field);
                if (type == PropertyType.PROPERTY) {
                    propertyInfos.add(propertyInfo);
                } else {
                    observableListInfos.add(propertyInfo);
                }
            }
        }

        return new ClassInfo(beanClass, propertyInfos, observableListInfos);
    }

    private enum PropertyType {PROPERTY, OBSERVABLE_LIST}
}
