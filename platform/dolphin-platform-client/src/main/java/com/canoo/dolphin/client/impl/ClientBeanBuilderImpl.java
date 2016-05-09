package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.AbstractBeanBuilder;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.PropertyImpl;
import com.canoo.dolphin.impl.collections.ObservableArrayList;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import com.canoo.dolphin.internal.info.PropertyInfo;
import com.canoo.dolphin.mapping.Property;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public class ClientBeanBuilderImpl extends AbstractBeanBuilder {

    public ClientBeanBuilderImpl(ClassRepository classRepository, BeanRepository beanRepository, ListMapper listMapper, PresentationModelBuilderFactory builderFactory, EventDispatcher dispatcher) {
        super(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
    }

    protected ObservableList create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper) {
        return new ObservableArrayList() {
            @Override
            protected void notifyInternalListeners(ListChangeEvent event) {
                listMapper.processEvent(observableListInfo, model.getId(), event);
            }
        };
    }


    protected Property create(final Attribute attribute, final PropertyInfo propertyInfo) {
        return new PropertyImpl<>(attribute, propertyInfo);
    }
}
