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
import java.lang.reflect.Field;
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
    private final Map<String, Property> method2prop;

    public DolphinModelInvocationHander(final Class modelClass, ServerDolphin dolphin, final BeanRepository beanRepository) {
        this.modelClass = modelClass;
        method2prop = new HashMap<>();
        method2propDesc = new HashMap<>();
        instance = (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, this);
        try {
            PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

            String modelType = DolphinUtils.getDolphinPresentationModelTypeForClass(modelClass);
            builder.withType(modelType);

            BeanInfo beanInfo = DolphinUtils.getBeanInfo(modelClass);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                    builder.withAttribute(descriptor.getName());
                }

            final PresentationModel model = builder.create();
            beanRepository.registerClass(modelClass);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                Attribute attribute = model.findAttributeByPropertyName(propertyDescriptor.getName());
                Property property = new PropertyImpl(beanRepository, attribute);
                String propertyName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
                method2prop.put(propertyName, property);
                method2propDesc.put(propertyDescriptor.getReadMethod(), propertyDescriptor);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null) {
                    method2propDesc.put(writeMethod, propertyDescriptor);
                }

            }

//            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
//                if (List.class.isAssignableFrom(descriptor.getPropertyType())) {
//                    final String propertyName = DolphinUtils.getDolphinAttributeName(descriptor);
//                    Property property = method2prop.get(propertyName);
//                    ObservableList observableList = new ObservableArrayList() {
//                        @Override
//                        protected void notifyInternalListeners(ListChangeEvent event) {
//                            if (beanRepository.getListMapper() != null) {
//                                beanRepository.getListMapper().processEvent(modelClass, model.getId(), propertyName, event);
//                            }
//                        }
//                    };
//                    property.set(observableList);
//
//                }
//            }

            beanRepository.getObjectPmToDolphinPm().put(instance, model);
            beanRepository.getDolphinIdToObjectPm().put(model.getId(), instance);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName());
        if (args != null && args.length > 0 && Collection.class.isAssignableFrom(args[0].getClass())) {
            throw new IllegalArgumentException("Must not set list property " + method.getName());
        }
        if ("hashCode".equalsIgnoreCase(method.getName())) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equalsIgnoreCase(method.getName())) {
            return System.identityHashCode(proxy) == System.identityHashCode(args[0]);
        }
        if ("toString".equalsIgnoreCase(method.getName())) {
            return DolphinModelInvocationHander.class.getName() + "@" + System.identityHashCode(proxy);
        }
        PropertyDescriptor descriptor1 = method2propDesc.get(method);

        String propertyName = DolphinUtils.getDolphinAttributeName(descriptor1);
        if (Property.class.isAssignableFrom(method.getReturnType())) {
            return method2prop.get(propertyName);
        } else if (Void.TYPE.equals(method.getReturnType())) {
            method2prop.get(propertyName).set(args[0]);
            return null;
        } else {
            return method2prop.get(propertyName).get();
        }
    }

    public T getInstance() {
        return instance;
    }
}
