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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class DolphinModelInvocationHander implements InvocationHandler {

    Map<String, Property<?>> method2prop;

    public DolphinModelInvocationHander(Class modelClass, ServerDolphin dolphin, final BeanRepository beanRepository) {
        method2prop = new HashMap<>();

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



            DolphinUtils.forAllMethods(modelClass, new DolphinUtils.MethodIterator() {
                @Override
                public void run(Method method, String attributeName) {

                    String propertyName = "propName";

                    Attribute attribute = model.findAttributeByPropertyName(attributeName);
                    Property property = new PropertyImpl(beanRepository, attribute);
                    method2prop.put(propertyName, property);
                }
            });


        } catch (Exception e) {
            throw new RuntimeException("Can't create bean", e);
        }




    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName());

        String propertyName = "propName";

        return method2prop.get(propertyName);
    }
}
