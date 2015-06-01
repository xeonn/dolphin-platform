package com.canoo.dolphin.impl;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.collections.ListMapper;
import com.canoo.dolphin.impl.collections.ObservableArrayList;
import com.canoo.dolphin.impl.info.ClassInfo;
import com.canoo.dolphin.impl.info.PropertyInfo;
import com.canoo.dolphin.mapping.Property;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;

/**
 * A {@code BeanBuilder} is responsible for building a Dolphin Bean that is specified as a class. The main
 * (and only public) method is {@link #create(Class)}, which expects the {@code Class} of the Dolphin Bean and
 * returns the generated Bean.
 *
 * The generated Dolphin Bean will be registered in the {@link BeanRepository}.
 */
public class BeanBuilder {

    private final Dolphin dolphin;
    private final ClassRepository classRepository;
    private final BeanRepository beanRepository;
    private final ListMapper listMapper;
    private final PresentationModelBuilderFactory builderFactory;

    public BeanBuilder(Dolphin dolphin, ClassRepository classRepository, BeanRepository beanRepository, ListMapper listMapper, PresentationModelBuilderFactory builderFactory) {
        this.dolphin = dolphin;
        this.classRepository = classRepository;
        this.beanRepository = beanRepository;
        this.listMapper = listMapper;
        this.builderFactory = builderFactory;
    }

    public <T> T create(Class<T> beanClass) {
        if (beanClass.isInterface()) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
        return createInstanceForClass(beanClass);
//        return beanClass.isInterface()?
//                new DolphinModelInvocationHander<T>(beanClass, dolphin, classRepository, beanRepository, listMapper).getInstance()
//                : createInstanceForClass(beanClass);
    }

    private <T> T createInstanceForClass(Class<T> beanClass) {
        try {
            final ClassInfo classInfo = classRepository.getClassInfo(beanClass);

            final T bean = beanClass.newInstance();
            final PresentationModel model = buildPresentationModel(classInfo);

            setupProperties(classInfo, bean, model);
            setupObservableLists(classInfo, bean, model);

            beanRepository.registerBean(bean, model);
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
