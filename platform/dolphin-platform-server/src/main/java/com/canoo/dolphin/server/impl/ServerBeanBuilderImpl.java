package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ListChangeListener;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.event.ValueChangeEvent;
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
import com.canoo.dolphin.server.impl.gc.GarbageCollection;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public class ServerBeanBuilderImpl extends AbstractBeanBuilder implements ServerBeanBuilder {

    private final GarbageCollection garbageCollection;

    public ServerBeanBuilderImpl(final ClassRepository classRepository, final BeanRepository beanRepository, final ListMapper listMapper, final PresentationModelBuilderFactory builderFactory, final EventDispatcher dispatcher, final GarbageCollection garbageCollection) {
        super(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        this.garbageCollection = Assert.requireNonNull(garbageCollection, "garbageCollection");
    }

    public <T> T createRootModel(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollection.onBeanCreated(bean, true);
        return bean;
    }

    @Override
    public <T> T create(Class<T> beanClass) {
        T bean = super.create(beanClass);
        garbageCollection.onBeanCreated(bean, false);
        return bean;
    }

    protected <T> ObservableList<T> create(final PropertyInfo observableListInfo, final PresentationModel model, final ListMapper listMapper) {
        final ObservableList<T> list = new ObservableArrayList<T>() {
            @Override
            protected void notifyInternalListeners(ListChangeEvent<T> event) {
                listMapper.processEvent(observableListInfo, model.getId(), event);
            }
        };

        list.onChanged(new ListChangeListener<T>() {
            @Override
            public void listChanged(ListChangeEvent<? extends T> event) {
                for(ListChangeEvent.Change<? extends T> c : event.getChanges()) {
                    if(c.isAdded()) {
                        for(Object added : list.subList(c.getFrom(), c.getTo())) {
                            garbageCollection.onAddedToList(list, added);
                        }
                    }
                    if(c.isRemoved()) {
                        for(Object removed : c.getRemovedElements()) {
                            garbageCollection.onRemovedFromList(list, removed);
                        }
                    }
                    if(c.isReplaced()) {
                        //??? TODO
                    }
                }
            }
        });

        return list;
    }

    protected <T> Property<T> create(final Attribute attribute, final PropertyInfo propertyInfo) {
        return new PropertyImpl<T>(attribute, propertyInfo) {

            @Override
            protected void notifyInternalListeners(ValueChangeEvent event) {
                super.notifyInternalListeners(event);
                garbageCollection.onPropertyValueChanged(event.getSource(), event.getOldValue(), event.getNewValue());
            }
        };
    }
}

