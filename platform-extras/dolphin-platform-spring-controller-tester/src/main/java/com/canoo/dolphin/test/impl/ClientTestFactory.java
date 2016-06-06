package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.test.ControllerTestException;
import com.canoo.dolphin.test.ControllerUnderTest;
import com.canoo.dolphin.util.Assert;

public class ClientTestFactory {

    public static <T> ControllerUnderTest<T> createController(ClientContext clientContext, String controllerName) {
        Assert.requireNonNull(clientContext, "clientContext");
        Assert.requireNonBlank(controllerName, "controllerName");
        try {
            final ControllerProxy<T> proxy = (ControllerProxy<T>) clientContext.createController(controllerName).get();
            return new ControllerUnderTest<T>() {
                @Override
                public T getModel() {
                    return proxy.getModel();
                }

                @Override
                public void invoke(String actionName, Param... params) {
                    try {
                        proxy.invoke(actionName, params).get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in action invocation", e);
                    }
                }

                @Override
                public void destroy() {
                    try {
                        proxy.destroy().get();
                    } catch (Exception e) {
                        throw new ControllerTestException("Error in destroy", e);
                    }
                }
            };
        } catch (Exception e) {
            throw new ControllerTestException("Can't create controller proxy", e);
        }
    }
}
