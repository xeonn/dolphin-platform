package com.canoo.dolphin.test.demo;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.test.AbstractSpringTest;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class DemoTest extends AbstractSpringTest {

    @Inject
    private ClientContext clientContext;

    @Test
    public void testTest() {
        try {
            ControllerProxy proxy = clientContext.createController("TestController").get();
            proxy.invoke("action").get();
            System.out.println(proxy);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
