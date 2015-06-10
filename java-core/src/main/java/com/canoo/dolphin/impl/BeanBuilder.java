package com.canoo.dolphin.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.impl.collections.ObservableArrayList;
import com.canoo.dolphin.impl.info.ClassInfo;
import com.canoo.dolphin.impl.info.PropertyInfo;
import com.canoo.dolphin.mapping.Property;
import org.opendolphin.core.*;

import static com.canoo.dolphin.impl.BeanRepository.UpdateSource.*;

/**
 * A {@code BeanBuilder} is responsible for building a Dolphin Bean that is specified as a class. The main
 * (and only public) method is {@link #create(Class)}, which expects the {@code Class} of the Dolphin Bean and
 * returns the generated Bean.
 *
 * The generated Dolphin Bean will be registered in the {@link BeanRepository}.
 */
public class BeanBuilder {

    private final ClassRepository classRepository;
    private final BeanRepository beanRepository;
    private final ListMapper listMapper;
    private final PresentationModelBuilderFactory builderFactory;

    public BeanBuilder(final ClassRepository classRepository, final BeanRepository beanRepository, ListMapper listMapper, PresentationModelBuilderFactory builderFactory, EventDispatcher dispatcher) {
        this.classRepository = classRepository;
        this.beanRepository = beanRepository;
        this.listMapper = listMapper;
        this.builderFactory = builderFactory;

        dispatcher.addAddedHandler(new EventDispatcher.DolphinEventHandler() {
            @Override
            public void onEvent(PresentationModel model) {
                final ClassInfo classInfo = classRepository.getClassInfo(model.getPresentationModelType());
                final Class<?> beanClass = classInfo.getBeanClass();

                createInstanceForClass(classInfo, beanClass, model, OTHER);
            }
        });
    }

    public <T> T create(Class<T> beanClass) {
        final ClassInfo classInfo = classRepository.getOrCreateClassInfo(beanClass);
        final PresentationModel model = buildPresentationModel(classInfo);

        return createInstanceForClass(classInfo, beanClass, model, SELF);
    }

    private <T> T createInstanceForClass(ClassInfo classInfo, Class<T> beanClass, PresentationModel model, BeanRepository.UpdateSource source) {
        try {
            final T bean = beanClass.newInstance();

            setupProperties(classInfo, bean, model);
            setupObservableLists(classInfo, bean, model);

            beanRepository.registerBean(bean, model, source);
            return bean;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create bean", e);
        }
    }

    private PresentationModel buildPresentationModel(ClassInfo classInfo) {
        final PresentationModelBuilder builder = builderFactory.createBuilder()
                .withType(classInfo.getModelType());

        classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
            @Override
            public void call(PropertyInfo propertyInfo) {
                builder.withAttribute(propertyInfo.getAttributeName());
            }
        });

        return builder.create();
    }

    private void setupProperties(ClassInfo classInfo, final Object bean, final PresentationModel model) {
        classInfo.forEachProperty(new ClassInfo.PropertyIterator() {
            @Override
            public void call(PropertyInfo propertyInfo) {
                final Attribute attribute = model.findAttributeByPropertyName(propertyInfo.getAttributeName());
                final Property property = new PropertyImpl<>(attribute, propertyInfo);
                propertyInfo.setPriviliged(bean, property);
            }
        });
    }

    private void setupObservableLists(ClassInfo classInfo, final Object bean, final PresentationModel model) {
        classInfo.forEachObservableList(new ClassInfo.PropertyIterator() {
            @Override
            public void call(final PropertyInfo observableListInfo) {
                final ObservableList observableList = new ObservableArrayList() {
                    @Override
                    protected void notifyInternalListeners(ListChangeEvent event) {
                        listMapper.processEvent(observableListInfo, model.getId(), event);
                    }
                };
                observableListInfo.setPriviliged(bean, observableList);
            }
        });
    }
}
