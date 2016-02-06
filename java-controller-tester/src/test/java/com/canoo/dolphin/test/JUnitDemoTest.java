package com.canoo.dolphin.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

public class JUnitDemoTest extends SpringJUnitControllerTest {

    private ControllerUnderTest<TestModel> controller;

    @Before
    public void initController() {
        controller = createControllerProxy("TestController");
    }

    @Test
    public void testTest() {
        Assert.assertEquals(null, controller.getModel().getValue());
        controller.invoke("action");
        Assert.assertEquals("Hello Dolphin Test", controller.getModel().getValue());
    }

    @After
    public void destroyController() {
        controller.destroy();
    }
}
