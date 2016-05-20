package com.canoo.dolphin.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNGDemoTest extends SpringTestNGControllerTest {

    @Test
    public void testCreation() {
        ControllerUnderTest<TestModel> controller = createControllerProxy("TestController");
        Assert.assertNotNull(controller);
        Assert.assertNotNull(controller.getModel());
        controller.destroy();
    }

    @Test
    public void testInteraction() {
        ControllerUnderTest<TestModel> controller = createControllerProxy("TestController");
        Assert.assertEquals(null, controller.getModel().getValue());
        controller.invoke("action");
        Assert.assertEquals("Hello Dolphin Test", controller.getModel().getValue());
        controller.destroy();
    }

    @Test
    public void testDestroy() {
        ControllerUnderTest<TestModel> controller = createControllerProxy("TestController");
        controller.destroy();
        try {
            controller.destroy();
            Assert.fail("Calling destroy() for a destroyed controller should throw an exception!");
        } catch (ControllerTestException e) {}
    }

    @Test
    public void testInvokeUnknownAction() {
        ControllerUnderTest<TestModel> controller = createControllerProxy("TestController");
        try {
            controller.invoke("unknownActionName");
            Assert.fail("Calling an unknown action should throw an exception!");
        } catch (ControllerTestException e) {}
        controller.destroy();
    }

    @Test
    public void testInvokeActionAfterDestroy() {
        ControllerUnderTest<TestModel> controller = createControllerProxy("TestController");
        controller.destroy();
        try {
            controller.invoke("add");
            Assert.fail("Calling an action after destroy should throw an exception!");
        } catch (ControllerTestException e) {}
    }

}
