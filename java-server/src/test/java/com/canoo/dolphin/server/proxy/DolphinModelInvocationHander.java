package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.PresentationModelBuilder;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.PropertyImpl;
import com.canoo.dolphin.server.impl.collections.ObservableArrayList;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class DolphinModelInvocationHander<T> implements InvocationHandler {

    private final T instance;
    private final Class modelClass;
    private final Map<Method, PropertyDescriptor> method2propDesc;
    private final Map<String, Property> propertyName2prop;
    private final Map<String, ObservableList> propertyName2list;

    public DolphinModelInvocationHander(final Class modelClass, ServerDolphin dolphin, final BeanRepository beanRepository) {
        this.modelClass = modelClass;
        propertyName2prop = new HashMap<>();
        propertyName2list = new HashMap<>();
        method2propDesc = new HashMap<>();

        instance = (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, this);
        try {
            PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

            builder.withType(DolphinUtils.getDolphinPresentationModelTypeForClass(modelClass));

            BeanInfo beanInfo = DolphinUtils.getBeanInfo(modelClass);

            validate(beanInfo);

            buildAttributes(builder, beanInfo);

            final PresentationModel model = builder.create();

            beanRepository.registerClass(modelClass);

            beanRepository.getObjectPmToDolphinPm().put(instance, model);
            beanRepository.getDolphinIdToObjectPm().put(model.getId(), instance);

            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String propName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
                Attribute attribute = model.findAttributeByPropertyName(propName);
                if(attribute == null) {
                    throw new RuntimeException("Attribute not found for property"+propName);
                }
                Property property = new PropertyImpl(beanRepository, attribute);
                String propertyName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
                propertyName2prop.put(propertyName, property);
                method2propDesc.put(propertyDescriptor.getReadMethod(), propertyDescriptor);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null) {
                    method2propDesc.put(writeMethod, propertyDescriptor);
                }

            }


            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (List.class.isAssignableFrom(descriptor.getPropertyType())) {
                    final String propertyName = DolphinUtils.getDolphinAttributeName(descriptor);
                    ObservableList observableList = new ObservableArrayList() {
                        @Override
                        protected void notifyInternalListeners(ListChangeEvent event) {
                            if (beanRepository.getListMapper() != null) {
                                beanRepository.getListMapper().processEvent(modelClass, model.getId(), propertyName, event);
                            }
                        }
                    };
                    propertyName2list.put(propertyName, observableList);
                }
            }
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    private void buildAttributes(PresentationModelBuilder builder, BeanInfo beanInfo) {
        //improve this with java 8 :)
        Set<String> propertyNames = new HashSet<>();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            propertyNames.add(DolphinUtils.getDolphinAttributeName(descriptor));
        }
        for (String propertyName : propertyNames) {
            builder.withAttribute(propertyName);
        }
    }

    private void validate(BeanInfo beanInfo) {
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
                if(descriptor.getWriteMethod() != null) {
                    throw new IllegalArgumentException("Collections should not be set, method: " + descriptor.getWriteMethod().getName());
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("hashCode".equalsIgnoreCase(method.getName())) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equalsIgnoreCase(method.getName())) {
            return System.identityHashCode(proxy) == System.identityHashCode(args[0]);
        }
        if ("toString".equalsIgnoreCase(method.getName())) {
            return modelClass.getName() + "@" + System.identityHashCode(proxy);
        }
        PropertyDescriptor descriptor = method2propDesc.get(method);

        String propertyName = DolphinUtils.getDolphinAttributeName(descriptor);
        if(isList(descriptor)) {
            return propertyName2list.get(propertyName);
        } else {
            if (Property.class.isAssignableFrom(method.getReturnType())) {
                return propertyName2prop.get(propertyName);
            } else if (Void.TYPE.equals(method.getReturnType())) {
                propertyName2prop.get(propertyName).set(args[0]);
                return null;
            } else {
                return propertyName2prop.get(propertyName).get();
            }
        }
    }

    private boolean isList(PropertyDescriptor descriptor) {
        return List.class.isAssignableFrom(descriptor.getPropertyType());
    }

    public T getInstance() {
        return instance;
    }
}
