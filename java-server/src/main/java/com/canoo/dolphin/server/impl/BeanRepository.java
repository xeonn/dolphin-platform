package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.impl.collections.ObservableArrayList;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.canoo.dolphin.server.impl.ClassRepository.FieldType.DOLPHIN_BEAN;

public class BeanRepository {

    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();
    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();
    private final ClassRepository classRepository;
    private final ServerDolphin dolphin;

    private ListMapper listMapper;

    public BeanRepository(ServerDolphin dolphin, ClassRepository classRepository) {
        this.dolphin = dolphin;
        this.classRepository = classRepository;
        listMapper = new ListMapper(dolphin, classRepository, this);
    }

    public void setListMapper(ListMapper listMapper) {
        if(listMapper != null) {
            listMapper.unregisterListeners();
        }
        this.listMapper = listMapper;
    }

    public Object getValue(Attribute attribute) {
        return mapDolphinToObjects(attribute, attribute.getValue());
    }

    public void setValue(Attribute attribute, Object value) {
        if (!(isManaged(value) || ReflectionHelper.isAllowedForUnmanaged(value.getClass()))) {
            throw new IllegalArgumentException(String.format("Cannot assign unmanaged bean instance of type %s to property %s", value.getClass().getName(), attribute.getPropertyName()));
        }
        ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(attribute, value);
        attribute.setValue(mapObjectsToDolphin(fieldType, value));
    }

    public void setValue(Class<?> beanClass, String attributeName, Attribute relationAttribute, Object value) {
        ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
        relationAttribute.setValue(mapObjectsToDolphin(fieldType, value));
    }

    public Object mapObjectsToDolphin(ClassRepository.FieldType fieldType, Object value) {
        switch (fieldType) {
            case ENUM:
                return ((Enum) value).ordinal();
            case DOLPHIN_BEAN:
                return objectPmToDolphinPm.get(value).getId();
            default:
                return value;
        }
    }

    public Object mapDolphinToObjects(Attribute attribute, Object value) {

        ClassRepository.FieldType fieldType = classRepository.getFieldType(attribute);
        Class<?> fieldClass = classRepository.getFieldClass(attribute);
        return mapDolphinToObjects(fieldType, fieldClass, value);
    }

    public Object mapDolphinToObjects(Class<?> beanClass, String attributeName, Object value) {
        return mapDolphinToObjects(classRepository.getFieldType(beanClass, attributeName), classRepository.getFieldClass(beanClass, attributeName), value);
    }

    public Object mapDolphinToObject(String type, Object value) {
        if (value == null) {
            return null;
        }
        return DOLPHIN_BEAN.name().equals(type)? dolphinIdToObjectPm.get(value.toString()) : value;
    }

    private Object mapDolphinToObjects(ClassRepository.FieldType fieldType, Class<?> fieldClass, Object value) {
        switch (fieldType) {
            case ENUM:
                try {
                    return fieldClass.getEnumConstants()[(Integer) value];
                } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
                    // do nothing
                }
                return null;
            case DOLPHIN_BEAN:
                return value == null ? null : dolphinIdToObjectPm.get(value.toString());
        }
        return value;
    }

    public boolean isManaged(Object bean) {
        PresentationModel model = objectPmToDolphinPm.get(bean);
        return model != null && dolphin.findPresentationModelById(model.getId()) != null;
    }

    public <T> T create(Class<T> beanClass) {
        if(beanClass.isInterface()) {
            return new DolphinModelInvocationHander<T>(beanClass, dolphin, this).getInstance();
        } else {

            return createInstanceForClass(beanClass);
        }
    }

    private <T> T createInstanceForClass(Class<T> beanClass) {
        try {
            classRepository.register(beanClass);

            T instance = beanClass.newInstance();

            PresentationModel model = buildPresentationModel(beanClass);

            forAllProperties(beanClass, instance, model);
            forAllObservableLists(beanClass, instance, model);

            objectPmToDolphinPm.put(instance, model);
            dolphinIdToObjectPm.put(model.getId(), instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    private <T> PresentationModel buildPresentationModel(Class<T> beanClass) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

        String modelType = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);
        builder.withType(modelType);

        DolphinUtils.forAllProperties(beanClass, new DolphinUtils.FieldIterator() {
            @Override
            public void run(Field field, String attributeName) {
                builder.withAttribute(attributeName);
            }
        });

        return builder.create();
    }

    private <T> void forAllProperties(Class<T> beanClass, final T instance, final PresentationModel model) {
        DolphinUtils.forAllProperties(beanClass, new DolphinUtils.FieldIterator() {
            @Override
            public void run(Field field, String attributeName) {
                Attribute attribute = model.findAttributeByPropertyName(attributeName);
                @SuppressWarnings("unchecked")
                Property property = new PropertyImpl(BeanRepository.this, attribute);
                ReflectionHelper.setPrivileged(field, instance, property);
            }
        });
    }

    private <T> void forAllObservableLists(final Class<T> beanClass, final T instance, final PresentationModel model) {
        DolphinUtils.forAllObservableLists(beanClass, new DolphinUtils.FieldIterator() {
            @SuppressWarnings("unchecked")
            @Override
            public void run(Field field, final String attributeName) {
                ObservableList observableList = new ObservableArrayList() {
                    @Override
                    protected void notifyInternalListeners(ListChangeEvent event) {
                        if (listMapper != null) {
                            listMapper.processEvent(beanClass, model.getId(), attributeName, event);
                        }
                    }
                };
                ReflectionHelper.setPrivileged(field, instance, observableList);
            }
        });
    }

    public void registerClass(Class beanClass) {
        classRepository.register(beanClass);
    }

    public <T> void delete(T bean) {
        PresentationModel model = objectPmToDolphinPm.remove(bean);
        if (model != null) {
            dolphinIdToObjectPm.remove(model.getId());
            dolphin.remove(model);
        }
    }

    public ListMapper getListMapper() {
        return listMapper;
    }

    public void deleteAll(Class<?> beanClass) {
        for (Object bean : findAll(beanClass)) {
            delete(bean);
        }
    }

    public <T> List<T> findAll(Class<T> beanClass) {
        List<T> ret = new ArrayList<>();
        List<ServerPresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass));
        for (ServerPresentationModel model : presentationModels) {
            ret.add((T) dolphinIdToObjectPm.get(model.getId()));
        }
        return ret;
    }

    public Map<String, Object> getDolphinIdToObjectPm() {
        return dolphinIdToObjectPm;
    }

    public Map<Object, PresentationModel> getObjectPmToDolphinPm() {
        return objectPmToDolphinPm;
    }

    public Object getBean(String sourceId) {
        return dolphinIdToObjectPm.get(sourceId);
    }
}
