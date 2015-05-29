//package com.canoo.dolphin.server.impl;
//
//import com.canoo.dolphin.impl.BeanRepository;
//import com.canoo.dolphin.impl.ClassRepository;
//import com.canoo.dolphin.impl.DolphinUtils;
//import com.canoo.dolphin.collections.ListChangeEvent;
//import com.canoo.dolphin.collections.ObservableList;
//import com.canoo.dolphin.impl.PropertyImpl;
//import com.canoo.dolphin.impl.ReflectionHelper;
//import com.canoo.dolphin.mapping.Property;
//import com.canoo.dolphin.impl.collections.ListMapper;
//import com.canoo.dolphin.impl.collections.ObservableArrayList;
//import com.canoo.dolphin.impl.info.ClassInfo;
//import com.canoo.dolphin.impl.info.PropertyInfo;
//import org.opendolphin.core.Attribute;
//import org.opendolphin.core.PresentationModel;
//import org.opendolphin.core.server.ServerDolphin;
//
//import java.beans.BeanInfo;
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//// TODO Replace with an implementation that does not rely on BeanInfo
//@Deprecated
//public class DolphinModelInvocationHander<T> implements InvocationHandler {
//
//    private final T instance;
//    private final Class modelClass;
//    private final Map<Method, PropertyDescriptor> method2propDesc;
//    private final Map<String, Property> propertyName2prop;
//    private final ListMapper listMapper;
//
//    private final Map<String, ObservableList> propertyName2list;
//
//    public DolphinModelInvocationHander(Class modelClass, ServerDolphin dolphin, ClassRepository classRepository, BeanRepository beanRepository, ListMapper listMapper) {
//        this.modelClass = modelClass;
//        this.listMapper = listMapper;
//        propertyName2prop = new HashMap<>();
//        propertyName2list = new HashMap<>();
//        method2propDesc = new HashMap<>();
//
//        instance =  (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, this);
//
//        final ClassInfo classInfo = classRepository.getClassInfo(modelClass);
//        try {
//            BeanInfo beanInfo = DolphinUtils.getBeanInfo(modelClass);
//
//            ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(dolphin);
//            builder.withType(DolphinUtils.getDolphinPresentationModelTypeForClass(modelClass));
//            buildAttributes(builder, beanInfo);
//
//            PresentationModel model = builder.create();
//
//            beanRepository.registerBean(instance, model);
//
//            forAllPropertyDescriptors(beanRepository, beanInfo, model, classInfo);
//        } catch (IllegalArgumentException iae) {
//            throw iae;
//        } catch (Exception e) {
//            throw new RuntimeException("Can't create bean", e);
//        }
//    }
//
//    private void forAllPropertyDescriptors(BeanRepository beanRepository, BeanInfo beanInfo, PresentationModel model, ClassInfo classInfo) {
//        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
//            String propertyName = DolphinUtils.getDolphinAttributeName(propertyDescriptor);
//            Attribute attribute = model.findAttributeByPropertyName(propertyName);
//            if(attribute == null) {
//                throw new RuntimeException("Attribute not found for property "+propertyName);
//            }
//            final PropertyInfo propertyInfo = isList(propertyDescriptor)? classInfo.getObservableListInfo(propertyName) : classInfo.getPropertyInfo(propertyName);
//            Property property = new PropertyImpl(attribute, propertyInfo);
//            propertyName2prop.put(propertyName, property);
//            method2propDesc.put(propertyDescriptor.getReadMethod(), propertyDescriptor);
//            Method writeMethod = propertyDescriptor.getWriteMethod();
//            if (writeMethod != null) {
//                method2propDesc.put(writeMethod, propertyDescriptor);
//            }
//            if (isList(propertyDescriptor)) {
//                ObservableList observableList = new CheckedObservableArrayList(beanRepository, model, propertyInfo);
//                propertyName2list.put(propertyName, observableList);
//            }
//        }
//    }
//
//    private void validateElement(Object element, BeanRepository beanRepository, String propertyName) {
//        if(!(beanRepository.isManaged(element) || ReflectionHelper.isAllowedForUnmanaged(element.getClass()))) {
//            throw new IllegalArgumentException(String.format("Cannot add unmanaged bean instance of type %s to list %s", element.getClass().getName(), propertyName));
//        }
//    }
//
//    private void buildAttributes(ServerPresentationModelBuilder builder, BeanInfo beanInfo) {
//        //improve this with java 8 :)
//        Set<String> propertyNames = new HashSet<>();
//        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
//            propertyNames.add(DolphinUtils.getDolphinAttributeName(descriptor));
//        }
//        for (String propertyName : propertyNames) {
//            builder.withAttribute(propertyName);
//        }
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        if ("hashCode".equalsIgnoreCase(method.getName())) {
//            return System.identityHashCode(proxy);
//        }
//        if ("equals".equalsIgnoreCase(method.getName())) {
//            return System.identityHashCode(proxy) == System.identityHashCode(args[0]);
//        }
//        if ("toString".equalsIgnoreCase(method.getName())) {
//            return modelClass.getName() + "@" + System.identityHashCode(proxy);
//        }
//        PropertyDescriptor descriptor = method2propDesc.get(method);
//
//        String propertyName = DolphinUtils.getDolphinAttributeName(descriptor);
//        if(isList(descriptor)) {
//            return propertyName2list.get(propertyName);
//        } else {
//            if (Property.class.isAssignableFrom(method.getReturnType())) {
//                return propertyName2prop.get(propertyName);
//            } else if (Void.TYPE.equals(method.getReturnType())) {
//                propertyName2prop.get(propertyName).set(args[0]);
//                return null;
//            } else {
//                return propertyName2prop.get(propertyName).get();
//            }
//        }
//    }
//
//    public Property<T> getProperty(String name) {
//        return propertyName2prop.get(name);
//    }
//
//    private boolean isList(PropertyDescriptor descriptor) {
//        return List.class.isAssignableFrom(descriptor.getPropertyType());
//    }
//
//    public T getInstance() {
//        return instance;
//    }
//
//    private class CheckedObservableArrayList extends ObservableArrayList {
//
//        private final BeanRepository beanRepository;
//        private final PresentationModel model;
//        private final PropertyInfo propertyInfo;
//
//        CheckedObservableArrayList(BeanRepository beanRepository, PresentationModel model, PropertyInfo propertyInfo) {
//            this.beanRepository = beanRepository;
//            this.model = model;
//            this.propertyInfo = propertyInfo;
//        }
//
//        @Override
//        protected void notifyInternalListeners(ListChangeEvent event) {
//            listMapper.processEvent(propertyInfo, model.getId(), event);
//        }
//
//        @Override
//        public void add(int index, Object element) {
//            validateElement(element, beanRepository, propertyInfo.getAttributeName());
//            super.add(index, element);
//        }
//
//        @Override
//        public Object set(int index, Object element) {
//            validateElement(element, beanRepository, propertyInfo.getAttributeName());
//            return super.set(index, element);
//        }
//    }
//}
