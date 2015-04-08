package com.canoo.dolphin.server;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.ListSyncer;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.ObservableArrayList;
import com.canoo.dolphin.server.impl.PropertyImpl;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanManager {

    private final ServerDolphin dolphin;

    //TODO: Vielleicht wären hier Map<Object, WeakReference<PresentationModel>> sinnvoll??
    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();

    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();

    private final ClassRepository classRepository;
    private final ListSyncer listSyncer;

    private final PropertyImpl.BeanManagerAccess propertyAccessor = new PropertyImpl.BeanManagerAccess() {
        @Override
        public Object getValue(Attribute attribute) {
            return map(attribute, attribute.getValue());
        }

        @Override
        public void setValue(Attribute attribute, Object value) {
            final ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(attribute, value);
            final Object attributeValue;
            switch (fieldType) {
                case ENUM:
                    classRepository.register(value.getClass());
                    attributeValue = ((Enum) value).ordinal();
                    break;
                case DOLPHIN_BEAN:
                    attributeValue = objectPmToDolphinPm.get(value).getId();
                    break;
                default:
                    attributeValue = value;
            }
            attribute.setValue(attributeValue);
        }

        @Override
        public Object map(Attribute attribute, Object value) {
            switch (classRepository.getFieldType(attribute)) {
                case ENUM:
                    final Class<?> clazz = classRepository.getFieldClass(attribute);
                    try {
                        return clazz.getEnumConstants()[(Integer) value];
                    } catch (NullPointerException | ClassCastException | IndexOutOfBoundsException ex) {
                        // do nothing
                    }
                    return null;
                case DOLPHIN_BEAN:
                    return value == null ? null : dolphinIdToObjectPm.get(value.toString());
            }
            return value;
        }
    };

    public BeanManager(final ServerDolphin dolphin, ClassRepository classRepository) {
        this.dolphin = dolphin;
        this.classRepository = classRepository;

        final ListSyncer.BeanManagerAccess listSyncerAccessor = new ListSyncer.BeanManagerAccess() {
            @Override
            public void setValue(Class<?> beanClass, String attributeName, Attribute relationAttribute, Object value) {
                final ClassRepository.FieldType fieldType = BeanManager.this.classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
                final Object attributeValue;
                switch (fieldType) {
                    case ENUM:
                        BeanManager.this.classRepository.register(value.getClass());
                        attributeValue = ((Enum) value).ordinal();
                        break;
                    case DOLPHIN_BEAN:
                        attributeValue = objectPmToDolphinPm.get(value).getId();
                        break;
                    default:
                        attributeValue = value;
                }
                relationAttribute.setValue(attributeValue);
            }
        };
        listSyncer = new ListSyncer(dolphin, listSyncerAccessor);

        dolphin.getModelStore().addModelStoreListener(ListSyncer.ADD_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object reference = model.findAttributeByPropertyName("element").getValue();

                    final Object bean = dolphinIdToObjectPm.get(sourceId);
                    final Field field = BeanManager.this.classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    final Object element = map(bean.getClass(), attributeName, reference);
                    list.internalAdd(pos, element);
                    dolphin.remove(model);
                }
            }
        });
        dolphin.getModelStore().addModelStoreListener(ListSyncer.DEL_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int from = (Integer) model.findAttributeByPropertyName("from").getValue();
                    final int to = (Integer) model.findAttributeByPropertyName("to").getValue();

                    final Object bean = dolphinIdToObjectPm.get(sourceId);
                    final Field field = BeanManager.this.classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    list.internalDelete(from, to);
                    dolphin.remove(model);
                }
            }
        });

        dolphin.getModelStore().addModelStoreListener(ListSyncer.SET_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object reference = model.findAttributeByPropertyName("element").getValue();

                    final Object bean = dolphinIdToObjectPm.get(sourceId);
                    final Field field = BeanManager.this.classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    final Object element = map(bean.getClass(), attributeName, reference);
                    list.internalReplace(pos, element);
                    dolphin.remove(model);
                }
            }
        });



    }

    private Object map(Class<?> beanClass, String attributeName, Object value) {
        switch (classRepository.getFieldType(beanClass, attributeName)) {
            case ENUM:
                final Class<?> clazz = classRepository.getFieldClass(beanClass, attributeName);
                try {
                    return clazz.getEnumConstants()[(Integer) value];
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
        if (model == null) {
            return false;
        }
        return (dolphin.findPresentationModelById(model.getId()) != null);
    }

    public <T> T create(final Class<T> beanClass) {
        try {
            classRepository.register(beanClass);

            final T instance = beanClass.newInstance();

            final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

            String modelType = DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass);
            builder.withType(modelType);

            DolphinUtils.forAllProperties(beanClass, new DolphinUtils.FieldIterator() {
                @Override
                public void run(Field field, String attributeName) {
                    builder.withAttribute(attributeName);
                }
            });

            final PresentationModel model = builder.create();

            DolphinUtils.forAllProperties(beanClass, new DolphinUtils.FieldIterator() {
                @Override
                public void run(Field field, String attributeName) {
                    Attribute attribute = model.findAttributeByPropertyName(attributeName);
                    @SuppressWarnings("unchecked")
                    Property property = new PropertyImpl(propertyAccessor, attribute);
                    DolphinUtils.setPrivileged(field, instance, property);
                }
            });

            DolphinUtils.forAllObservableLists(beanClass, new DolphinUtils.FieldIterator() {
                @SuppressWarnings("unchecked")
                @Override
                public void run(Field field, final String attributeName) {
                    ObservableList observableList = new ObservableArrayList() {
                        @Override
                        protected void notifyInternalListeners(ListChangeEvent event) {
                            listSyncer.processEvent(beanClass, model.getId(), attributeName, event);
                        }
                    };
                    DolphinUtils.setPrivileged(field, instance, observableList);
                }
            });

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
}
