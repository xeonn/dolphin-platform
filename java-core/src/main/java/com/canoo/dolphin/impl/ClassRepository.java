package com.canoo.dolphin.impl;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.impl.info.ClassInfo;
import com.canoo.dolphin.impl.info.ClassPropertyInfo;
import com.canoo.dolphin.impl.info.PropertyInfo;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code ClassRepository} manages {@link ClassInfo} objects for all registered Dolphin Beans. A {@code ClassInfo}
 * object keeps information on class level about the properties and ObservableLists of a DolphinBean.
 */
public class ClassRepository {

    public enum FieldType {UNKNOWN, BASIC_TYPE, DOLPHIN_BEAN}

    private final Dolphin dolphin;
    private final PresentationModelBuilderFactory builderFactory;
    private final Converters.Converter initialConverter;

    private final Map<Class<?>, ClassInfo> classInfoMap = new HashMap<>();

    public ClassRepository(Dolphin dolphin, BeanRepository beanRepository, PresentationModelBuilderFactory builderFactory) {
        this.dolphin = dolphin;
        this.builderFactory = builderFactory;
        this.initialConverter = new Converters(beanRepository).getUnknownTypeConverter();
    }

    public ClassInfo getClassInfo(final Class<?> beanClass) {
        final ClassInfo existingClassInfo = classInfoMap.get(beanClass);
        if (existingClassInfo != null) {
            return existingClassInfo;
        }

        final ClassInfo newClassInfo;
        if (beanClass.isInterface()) {
            throw new UnsupportedOperationException("Not implmented yet");
//            final PresentationModel model = createPresentationModelForInterface(beanClass);
//            newClassInfo = createClassInfoForInterface(beanClass, model);
        } else {
            final PresentationModel model = createPresentationModelForClass(beanClass);
            newClassInfo = createClassInfoForClass(beanClass, model);

        }

        classInfoMap.put(beanClass, newClassInfo);
        return newClassInfo;
    }

//    private PresentationModel createPresentationModelForInterface(Class<?> beanClass) {
//        final BeanInfo beanInfo = DolphinUtils.getBeanInfo(beanClass);
//
//        final String id = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);
//        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin)
//                .withId(id)
//                .withType(DolphinConstants.DOLPHIN_BEAN);
//
//        for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
//            final String attributeName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
//            builder.withAttribute(attributeName, FieldType.UNKNOWN.ordinal(), Tag.VALUE);
//        }
//
//        return builder.create();
//    }

//    private ClassInfo createClassInfoForInterface(Class<?> beanClass, PresentationModel model) {
//        final BeanInfo beanInfo = DolphinUtils.getBeanInfo(beanClass);
//
//        final List<PropertyInfo> propertyInfos = new ArrayList<>();
//        final List<PropertyInfo> observableListInfos = new ArrayList<>();
//
//        for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
//            final String attributeName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
//            final Attribute attribute = model.findAttributeByPropertyName(attributeName);
//            final PropertyInfo propertyInfo = new InterfacePropertyInfo(attribute, attributeName, initialConverter, propertyDescriptor);
//            if (List.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
//                observableListInfos.add(propertyInfo);
//            } else {
//                propertyInfos.add(propertyInfo);
//            }
//        }
//
//        return new ClassInfo(beanClass, propertyInfos, observableListInfos);
//    }

    private PresentationModel createPresentationModelForClass(Class<?> beanClass) {
        final String id = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);
        final PresentationModelBuilder builder = builderFactory.createBuilder()
                .withId(id)
                .withType(DolphinConstants.DOLPHIN_BEAN);

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
