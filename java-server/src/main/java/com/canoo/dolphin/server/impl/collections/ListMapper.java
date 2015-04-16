package com.canoo.dolphin.server.impl.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.server.PresentationModelBuilder;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.DolphinConstants;
import com.canoo.dolphin.server.impl.DolphinUtils;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Field;
import java.util.List;

public class ListMapper {

    private final ServerDolphin dolphin;
    private final BeanRepository beanRepository;
    private final ClassRepository classRepository;

    public ListMapper(ServerDolphin dolphin, final ClassRepository classRepository, BeanRepository beanRepository) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;
        this.classRepository = classRepository;
        this.beanRepository.setListMapper(this);

        dolphin.getModelStore().addModelStoreListener(DolphinConstants.ADD_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object reference = model.findAttributeByPropertyName("element").getValue();

                    final Object bean = ListMapper.this.beanRepository.getBean(sourceId);
                    final Field field = classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    final Object element = ListMapper.this.beanRepository.mapDolphinToObjects(bean.getClass(), attributeName, reference);
                    list.internalAdd(pos, element);
                    ListMapper.this.dolphin.remove(model);
                }
            }
        });
        dolphin.getModelStore().addModelStoreListener(DolphinConstants.DEL_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int from = (Integer) model.findAttributeByPropertyName("from").getValue();
                    final int to = (Integer) model.findAttributeByPropertyName("to").getValue();

                    final Object bean = ListMapper.this.beanRepository.getBean(sourceId);
                    final Field field = classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    list.internalDelete(from, to);
                    ListMapper.this.dolphin.remove(model);
                }
            }
        });

        dolphin.getModelStore().addModelStoreListener(DolphinConstants.SET_FROM_CLIENT, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent modelStoreEvent) {
                if (modelStoreEvent.getType() == ModelStoreEvent.Type.ADDED) {
                    final PresentationModel model = modelStoreEvent.getPresentationModel();

                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();
                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object reference = model.findAttributeByPropertyName("element").getValue();

                    final Object bean = ListMapper.this.beanRepository.getBean(sourceId);
                    final Field field = classRepository.getField(bean.getClass(), attributeName);
                    final ObservableArrayList list = (ObservableArrayList) DolphinUtils.getPrivileged(field, bean);
                    final Object element = ListMapper.this.beanRepository.mapDolphinToObjects(bean.getClass(), attributeName, reference);
                    list.internalReplace(pos, element);
                    ListMapper.this.dolphin.remove(model);
                }
            }
        });
    }

    public void processEvent(Class<?> beanClass, String sourceId, String attributeName, ListChangeEvent<?> evt) {
        for (final ListChangeEvent.Change<?> change : evt.getChanges()) {

            final int to = change.getTo();
            int from = change.getFrom();
            int removedCount = change.getRemovedElements().size();

            if (change.isReplaced()) {
                final int n = Math.min(to - from, removedCount);
                final List<?> newElements = evt.getSource().subList(from, from + n);
                int pos = from;
                for (final Object element : newElements) {
                    sendReplace(beanClass, sourceId, attributeName, pos++, element);
                }
                from += n;
                removedCount -= n;
            }
            if (to > from) {
                final List<?> newElements = evt.getSource().subList(from, to);
                int pos = from;
                for (final Object element : newElements) {
                    sendAdd(beanClass, sourceId, attributeName, pos++, element);
                }
            } else if (removedCount > 0) {
                sendRemove(sourceId, attributeName, from, from + removedCount);
            }
        }
    }

    private void sendAdd(Class<?> beanClass, String sourceId, String attributeName, int pos, Object value) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        final ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
        final Object element = beanRepository.mapObjectsToDolphin(fieldType, value);
        builder.withType(DolphinConstants.ADD_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }

    private void sendRemove(String sourceId, String attributeName, int from, int to) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        builder.withType(DolphinConstants.DEL_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("from", from)
                .withAttribute("to", to)
                .create();
    }

    private void sendReplace(Class<?> beanClass, String sourceId, String attributeName, int pos, Object value) {
        final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);
        final ClassRepository.FieldType fieldType = classRepository.calculateFieldTypeFromValue(beanClass, attributeName, value);
        final Object element = beanRepository.mapObjectsToDolphin(fieldType, value);
        builder.withType(DolphinConstants.SET_FROM_SERVER)
                .withAttribute("source", sourceId)
                .withAttribute("attribute", attributeName)
                .withAttribute("pos", pos)
                .withAttribute("element", element)
                .create();
    }
}
