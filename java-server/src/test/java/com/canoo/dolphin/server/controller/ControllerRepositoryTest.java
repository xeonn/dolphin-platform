package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.server.event.impl.ServerPushController;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 17.02.16.
 */
public class ControllerRepositoryTest {

    @Test
    public void testExistingControllers() {
        ControllerRepository controllerRepository = new ControllerRepository();
        Class<?> controllerClass = controllerRepository.getControllerClassForName(ServerPushController.NAME);
        assertNotNull(controllerClass);
        assertEquals(controllerClass, ServerPushController.class);

        controllerClass = controllerRepository.getControllerClassForName(TestController.class.getName());
        assertNotNull(controllerClass);
        assertEquals(controllerClass, TestController.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongControllersName() {
        ControllerRepository controllerRepository = new ControllerRepository();
        Class<?> controllerClass = controllerRepository.getControllerClassForName("WrongControllerName");
    }

}
