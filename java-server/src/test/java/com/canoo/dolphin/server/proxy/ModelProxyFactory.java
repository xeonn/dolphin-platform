package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import org.opendolphin.core.server.ServerDolphin;

import java.lang.reflect.Proxy;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ModelProxyFactory {

    private ServerDolphin serverDolphin;
    private BeanRepository beanRepository;

    public ModelProxyFactory(ServerDolphin serverDolphin, BeanRepository beanRepository) {
        this.serverDolphin = serverDolphin;
        this.beanRepository = beanRepository;
    }





    public <T> T create(Class<T> modelClass) {
        return (T) Proxy.newProxyInstance(modelClass.getClassLoader(), new Class[]{modelClass}, new DolphinModelInvocationHander(modelClass, serverDolphin,beanRepository));
    }

}
