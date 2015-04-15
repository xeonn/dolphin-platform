package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.server.impl.PropertyImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class DolphinModelInvocationHander implements InvocationHandler {

    Map<Method, Property<?>> method2prop = new HashMap<Method, Property<?>>(){
        @Override
        public Property<?> get(Object key) {
            Property<?> value = (Property<?>)super.get(key);
            if(value == null) {
                value = null;
                put((Method)key, value);
            }
            return value;
        }
    };

    public DolphinModelInvocationHander() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName());

        return method2prop.get(method);
    }
}
