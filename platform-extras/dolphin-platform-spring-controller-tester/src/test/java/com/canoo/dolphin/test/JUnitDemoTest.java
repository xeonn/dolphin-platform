package com.canoo.dolphin.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JUnitDemoTest extends SpringJUnitControllerTest {

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
        assertEquals("Hello Dolphin Test", controller.getModel().getValue());
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
}
