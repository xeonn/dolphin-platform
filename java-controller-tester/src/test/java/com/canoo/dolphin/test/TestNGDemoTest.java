package com.canoo.dolphin.test;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestNGDemoTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<TestModel> controller;

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
