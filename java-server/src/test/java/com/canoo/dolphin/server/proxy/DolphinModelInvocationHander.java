package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.PresentationModelBuilder;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.DolphinUtils;
import com.canoo.dolphin.server.impl.PropertyImpl;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class DolphinModelInvocationHander<T> implements InvocationHandler {

    private final T instance;

    Map<String, Property<?>> method2prop;

    public DolphinModelInvocationHander(Class modelClass, ServerDolphin dolphin, final BeanRepository beanRepository) {
        method2prop = new HashMap<>();
        instance = (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, this);
        try {
            final PresentationModelBuilder builder = new PresentationModelBuilder(dolphin);

            final String modelType = DolphinUtils.getDolphinPresentationModelTypeForClass(modelClass);
            builder.withType(modelType);


            DolphinUtils.forAllMethods(modelClass, new DolphinUtils.MethodIterator() {
                @Override
                public void run(Method method, String attributeName) {
                    builder.withAttribute(attributeName);
                }
            });

            final PresentationModel model = builder.create();
            beanRepository.registerClass(modelClass);
            DolphinUtils.forAllMethods(modelClass, new DolphinUtils.MethodIterator() {
                @Override
                public void run(Method method, String attributeName) {
                    String propertyName = DolphinUtils.getDolphinAttributePropertyNameForMethod(method);

                    Attribute attribute = model.findAttributeByPropertyName(attributeName);
                    Property property = new PropertyImpl(beanRepository, attribute);
                    method2prop.put(propertyName, property);
                }
            });
            beanRepository.getObjectPmToDolphinPm().put(instance, model);
            beanRepository.getDolphinIdToObjectPm().put(model.getId(), instance);
        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("hashCode".equalsIgnoreCase(method.getName())) {

            return System.identityHashCode(proxy);
        }
        if("equals".equalsIgnoreCase(method.getName())) {
            return args[0].equals(proxy);
        }
        if ("toString".equalsIgnoreCase(method.getName())) {
            return DolphinModelInvocationHander.class.getName() + "@" + System.identityHashCode(proxy);
        }
        System.out.println(method.getName());

        String propertyName = DolphinUtils.getDolphinAttributePropertyNameForMethod(method);

        return method2prop.get(propertyName);
    }

    public T getInstance() {
        return instance;
    }
}
