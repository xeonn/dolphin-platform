package com.canoo.dolphin.test;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.test.impl.DolphinPlatformSpringTestBootstrap;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

@WebAppConfiguration
@SpringApplicationConfiguration(classes = DolphinPlatformSpringTestBootstrap.class)
public abstract class AbstractSpringTest extends AbstractTestNGSpringContextTests {

    @Inject
    private ClientContext clientContext;

    protected <T> ControllerAccess<T> createControllerProxy(String controllerName) {
        try {
            final ControllerProxy<T> proxy = (ControllerProxy<T>) clientContext.createController(controllerName).get();
            return new ControllerAccess<T>() {
                @Override
                public T getModel() {
                    return proxy.getModel();
                }

                @Override
                public void invoke(String actionName, Param... params) {
                    try {
                        proxy.invoke(actionName, params).get();
                    } catch (Exception e) {
                        throw new RuntimeException("Error in action", e);
                    }
                }

                @Override
                public void destroy() {
                    try {
                        proxy.destroy().get();
                    } catch (Exception e) {
                        throw new RuntimeException("Error in destroy", e);
                    }
                }
            };
        } catch (Exception e) {
           throw new RuntimeException("Can't create controller proxy", e);
        }
    }
}

