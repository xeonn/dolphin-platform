/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.impl.collections;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.DolphinEventHandler;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.internal.info.ClassInfo;
import com.canoo.dolphin.internal.info.PropertyInfo;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;

import java.util.List;

public class ListMapperImpl implements ListMapper {

    private final Dolphin dolphin;
    private final BeanRepository beanRepository;
    private final ClassRepository classRepository;
    protected final PresentationModelBuilderFactory builderFactory;

    public ListMapperImpl(Dolphin dolphin, ClassRepository classRepository, BeanRepository beanRepository, PresentationModelBuilderFactory builderFactory, EventDispatcher dispatcher) {
        this.dolphin = dolphin;
        this.beanRepository = beanRepository;
        this.classRepository = classRepository;
        this.builderFactory = builderFactory;

        dispatcher.addListElementAddHandler(createAddListener());
        dispatcher.addListElementDelHandler(createDelListener());
        dispatcher.addListElementSetHandler(createSetListener());
    }

    private DolphinEventHandler createAddListener() {
        return new DolphinEventHandler() {

            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(PresentationModel model) {
                try {
                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();

                    final Object bean = beanRepository.getBean(sourceId);
                    final ClassInfo classInfo = classRepository.getOrCreateClassInfo(bean.getClass());
                    final PropertyInfo observableListInfo = classInfo.getObservableListInfo(attributeName);

                    final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object dolphinValue = model.findAttributeByPropertyName("element").getValue();

                    final Object value = observableListInfo.convertFromDolphin(dolphinValue);
                    list.internalAdd(pos, value);
                } catch (NullPointerException | ClassCastException ex) {
                    System.out.println("Invalid LIST_ADD command received: " + model);
                } finally {
                    if (model != null) {
                        dolphin.remove(model);
                    }
                }
            }
        };
    }

    private DolphinEventHandler createDelListener() {
        return new DolphinEventHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(PresentationModel model) {
                try {
                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();

                    final Object bean = beanRepository.getBean(sourceId);
                    final ClassInfo classInfo = classRepository.getOrCreateClassInfo(bean.getClass());
                    final PropertyInfo observableListInfo = classInfo.getObservableListInfo(attributeName);

                    final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

                    int from = (Integer) model.findAttributeByPropertyName("from").getValue();
                    int to = (Integer) model.findAttributeByPropertyName("to").getValue();
                    list.internalDelete(from, to);
                } catch (NullPointerException | ClassCastException ex) {
                    System.out.println("Invalid LIST_ADD command received: " + model);
                } finally {
                    if (model != null) {
                        dolphin.remove(model);
                    }
                }
            }
        };
    }

    private DolphinEventHandler createSetListener() {
        return new DolphinEventHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onEvent(PresentationModel model) {
                try {
                    final String sourceId = model.findAttributeByPropertyName("source").getValue().toString();
                    final String attributeName = model.findAttributeByPropertyName("attribute").getValue().toString();

                    final Object bean = beanRepository.getBean(sourceId);
                    final ClassInfo classInfo = classRepository.getOrCreateClassInfo(bean.getClass());
                    final PropertyInfo observableListInfo = classInfo.getObservableListInfo(attributeName);

                    final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

                    final int pos = (Integer) model.findAttributeByPropertyName("pos").getValue();
                    final Object dolphinValue = model.findAttributeByPropertyName("element").getValue();
                    final Object value = observableListInfo.convertFromDolphin(dolphinValue);
                    list.internalReplace(pos, value);
                } catch (NullPointerException | ClassCastException ex) {
                    System.out.println("Invalid LIST_ADD command received: " + model);
                } finally {
                    if (model != null) {
                        dolphin.remove(model);
                    }
                }
            }
        };
    }

    @Override
    public void processEvent(PropertyInfo observableListInfo, String sourceId, ListChangeEvent<?> event) {
        final String attributeName = observableListInfo.getAttributeName();

        for (final ListChangeEvent.Change<?> change : event.getChanges()) {

            final int from = change.getFrom();
            final int to = from + change.getRemovedElements().size();
            final List<?> newElements = event.getSource().subList(from, change.getTo());
            sendSplice(observableListInfo, sourceId, attributeName, from, to, newElements);
        }
    }

    private void sendSplice(PropertyInfo observableListInfo, String sourceId, String attributeName, int from, int to, List<?> newElements) {
        final int count = newElements.size();
        final PresentationModelBuilder builder = builderFactory.createBuilder();
        builder.withType(PlatformConstants.LIST_SPLICE)
               .withAttribute("source", sourceId)
               .withAttribute("attribute", attributeName)
               .withAttribute("from", from)
               .withAttribute("to", to)
               .withAttribute("count", count);
        int i = 0;
        for (final Object current : newElements) {
            builder.withAttribute(Integer.toString(i++), observableListInfo.convertToDolphin(current));
        }
        builder.create();
    }
}
