/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.controller;

import com.canoo.dolphin.server.DolphinController;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
            throw new RuntimeException(ControllerRepository.class.getName() + " already initialized");
        }
        controllersClasses = new HashMap<>();

        ConfigurationBuilder configuration = ConfigurationBuilder.build(ControllerRepository.class.getClassLoader());

        //Special case for JBOSS Application server to get all classes
        try {
            Enumeration<URL> res = ControllerRepository.class.getClassLoader().getResources("");
            while (res.hasMoreElements()) {
                configuration.getUrls().add(res.nextElement());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error in Dolphin Platform controller class scan", e);
        }


        //Remove native libs (will be added on Mac in a Spring Boot app)
        Set<URL> urls = configuration.getUrls();
        List<URL> toRemove = new ArrayList<>();
        for (URL url : urls) {
            if (url.toString().endsWith(".jnilib")) {
                toRemove.add(url);
            }
        }
        for (URL url : toRemove) {
            configuration.getUrls().remove(url);
        }

        Reflections reflections = new Reflections(configuration);
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
        if (!initialized) {
            throw new IllegalStateException(ControllerRepository.class.getName() + " has not been initialized!");
        }
        return controllersClasses.get(name);
    }
}
