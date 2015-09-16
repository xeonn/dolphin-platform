package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.server.DolphinController;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by hendrikebbers on 16.09.15.
 */
public class ControllerRepository {

    private static Map<String, Class> controllersClasses;

    private static boolean initialized = false;

    private ControllerRepository() {
    }

    public static synchronized void init() {
        if (initialized) {
            throw new RuntimeException(ControllerHandler.class.getName() + " already initialized");
        }
        controllersClasses = new HashMap<>();
        Reflections reflections = new Reflections();
        Set<Class<?>> foundControllerClasses = reflections.getTypesAnnotatedWith(DolphinController.class);
        for (Class<?> controllerClass : foundControllerClasses) {
            String name = controllerClass.getName();
            if (controllerClass.getAnnotation(DolphinController.class).value() != null && !controllerClass.getAnnotation(DolphinController.class).value().trim().isEmpty()) {
                name = controllerClass.getAnnotation(DolphinController.class).value();
            }
            controllersClasses.put(name, controllerClass);
        }
        initialized = true;
    }

    public static synchronized Class getControllerClassForName(String name) {
        return controllersClasses.get(name);
    }
}
