package com.canoo.dolphin.server.proxy;

import java.lang.reflect.Proxy;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ModelProxyFactory {

    public <T> T create(Class<T> modelClass) {
        return (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, new DolphinModelInvocationHander());
    }

}
