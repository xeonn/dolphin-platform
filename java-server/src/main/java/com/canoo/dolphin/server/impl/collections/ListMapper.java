package com.canoo.dolphin.server.impl.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.server.impl.PresentationModelBuilder;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.DolphinConstants;
import com.canoo.dolphin.server.impl.DolphinUtils;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ListMapper {

    private final ServerDolphin dolphin;
    private final BeanRepository beanRepository;
    private final ClassRepository classRepository;
    private final ModelStoreListener addListener;
    private final ModelStoreListener deleteListener;
    private final ModelStoreListener setListener;

    public ListMapper(ServerDolphin dolphin, ClassRepository classRepository, BeanRepository beanRepository) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;
        this.classRepository = classRepository;
        this.beanRepository.setListMapper(this);

        addListener = createAddListener();
        deleteListener = createDeleteListener();
        setListener = createSetListener();
        dolphin.getModelStore().addModelStoreListener(DolphinConstants.ADD_FROM_CLIENT, addListener);
        dolphin.getModelStore().addModelStoreListener(DolphinConstants.DEL_FROM_CLIENT, deleteListener);
        dolphin.getModelStore().addModelStoreListener(DolphinConstants.SET_FROM_CLIENT, setListener);
    }

    private ModelStoreListener createAddListener() {
        return new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    PresentationModel model = modelStoreEvent.getPresentationModel();

                    String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    Object reference = model.findAttributeByPropertyName("element").getValue();

                    ObservableArrayList list = getObservableList(attributeName, model);
                    Object element = beanRepository.mapDolphinToObjects(getBean(model).getClass(), attributeName, reference);
                    list.internalAdd(pos, element);
                    dolphin.remove(model);
                }
            }
        };
    }

    private ModelStoreListener createDeleteListener() {
        return new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    PresentationModel model = modelStoreEvent.getPresentationModel();
                    String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();

                    ObservableArrayList list = getObservableList(attributeName, model);
                    int from = (Integer) model.findAttributeByPropertyName("from").getValue();
                    int to = (Integer) model.findAttributeByPropertyName("to").getValue();
                    list.internalDelete(from, to);
                    dolphin.remove(model);
                }
            }
        };
    }

    private ModelStoreListener createSetListener() {
        return new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    PresentationModel model = modelStoreEvent.getPresentationModel();

                    String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    Object reference = model.findAttributeByPropertyName("element").getValue();

                    ObservableArrayList list = getObservableList(attributeName, model);
                    Object bean = getBean(model);
                    Object element = beanRepository.mapDolphinToObjects(bean.getClass(), attributeName, reference);
                    list.internalReplace(pos, element);
                    dolphin.remove(model);
                }
            }
        };
    }

    private ObservableArrayList getObservableList(String attributeName, PresentationModel model) {
        Object bean = getBean(model);
        if (bean.getClass().isInterface()) {
            return saveInvoke(bean, classRepository.getPropertyDescriptor(bean.getClass(), attributeName));
        }else {
            Field field = classRepository.getField(bean.getClass(), attributeName);
            return (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
        }
    }

    private Object getBean(PresentationModel model) {
        String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
        return beanRepository.getBean(sourceId);
    }

    private <T>  T saveInvoke(Object bean, PropertyDescriptor field)  {
        try {
            return (T)field.getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void processEvent(Class<?> beanClass, String sourceId, String attributeName, ListChangeEvent<?> evt) {
        for (ListChangeEvent.Change<?> change : evt.getChanges()) {

            int to = change.getTo();
            int from = change.getFrom();
            int removedCount = change.getRemovedElements().size();

            if (change.isReplaced()) {
                int n = Math.min(to - from, removedCount);
                List<?> newElements = evt.getSource().subList(from, from + n);
                int pos = from;
                for (Object element : newElements) {
                    sendReplace(beanClass, sourceId, attributeName, pos++, element);
                }
                from += n;
                removedCount -= n;
            }
            if (to > from) {
                List<?> newElements = evt.getSource().subList(from, to);
                int pos = from;
                for (Object element : newElements) {
                    sendAdd(beanClass, sourceId, attributeName, pos++, element);
                }
            } else if (removedCount > 0) {
                sendRemove(sourceId, attributeName, from, from + removedCount);
            }
        }
    }

    private void sendAdd(Class<?> beanClass, String sourceId, String attributeName, int pos, Object value) {
        PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
        Object element = beanRepository.mapObjectsToDolphin(fieldType, value);
        builder.withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }

    private void sendRemove(String sourceId, String attributeName, int from, int to) {
        PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        builder.withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("from", from)
                .withAttribute("to", to)
                .create();
    }

    private void sendReplace(Class<?> beanClass, String sourceId, String attributeName, int pos, Object value) {
        PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
        Object element = beanRepository.mapObjectsToDolphin(fieldType, value);
        builder.withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }

    public void unregisterListeners() {
        dolphin.getServerModelStore().removeModelStoreListener(DolphinConstants.ADD_FROM_CLIENT,addListener);
        dolphin.getServerModelStore().removeModelStoreListener(DolphinConstants.SET_FROM_CLIENT,setListener);
        dolphin.getServerModelStore().removeModelStoreListener(DolphinConstants.DEL_FROM_CLIENT,deleteListener);
    }
}
