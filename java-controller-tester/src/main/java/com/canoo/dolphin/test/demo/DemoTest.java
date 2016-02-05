package com.canoo.dolphin.test.demo;

import com.canoo.dolphin.test.AbstractSpringTest;
import com.canoo.dolphin.test.ControllerAccess;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DemoTest extends AbstractSpringTest {

    private ControllerAccess<TestModel> controller;

    @BeforeMethod
    public void initController() {
        controller = createControllerProxy("TestController");
    }

    @Test
    public void testTest() {
        Assert.assertEquals(null, controller.getModel().getValue());
        controller.invoke("action");
        Assert.assertEquals("Hello Dolphin Test", controller.getModel().getValue());
    }

    @AfterMethod
    public void destroyController() {
        controller.destroy();
    }

}
