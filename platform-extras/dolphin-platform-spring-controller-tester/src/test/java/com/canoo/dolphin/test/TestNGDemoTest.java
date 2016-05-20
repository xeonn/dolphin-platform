package com.canoo.dolphin.test;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class TestNGDemoTest extends SpringTestNGControllerTest {

    @Test
    public void testCreation() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        assertNotNull(controller);
        assertNotNull(controller.getModel());
        controller.destroy();
    }

    @Test
    public void testInteraction() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        assertEquals(null, controller.getModel().getValue());
        controller.invoke("action");
        assertEquals(controller.getModel().getValue(), "Hello Dolphin Test");
        controller.destroy();
    }

    @Test
    public void testDestroy() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.destroy();
        try {
            controller.destroy();
            fail("Calling destroy() for a destroyed controller should throw an exception!");
        } catch (ControllerTestException e) {}
    }

    @Test
    public void testInvokeUnknownAction() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        try {
            controller.invoke("unknownActionName");
            fail("Calling an unknown action should throw an exception!");
        } catch (ControllerTestException e) {}
        controller.destroy();
    }

    @Test
    public void testInvokeActionAfterDestroy() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.destroy();
        try {
            controller.invoke("add");
            fail("Calling an action after destroy should throw an exception!");
        } catch (ControllerTestException e) {}
    }

    @Test
    public void testAddToList() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.invoke("addToList");
        assertEquals(controller.getModel().getItems().size(), 1);
        assertTrue(controller.getModel().getItems().contains("Hallo"));
        controller.destroy();
    }

    @Test
    public void testAddBeanToList() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.invoke("addBeanToList");
        assertEquals(controller.getModel().getInternModels().size(), 1);
        assertEquals(controller.getModel().getInternModels().get(0).getValue(), "I'm a subbean");
        controller.destroy();
    }

    @Test
    public void testEventBus() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.invoke("sendEvent");

        //Still a workaround to wait till next request
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(controller.getModel().getValue(), "changed by eventBus!");
        controller.destroy();
    }
}
