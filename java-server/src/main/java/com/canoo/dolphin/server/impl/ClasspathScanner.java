package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.util.Assert;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class ClasspathScanner {

    private Reflections reflections;

    private static ClasspathScanner instance = new ClasspathScanner();

    private ClasspathScanner() {
        ConfigurationBuilder configuration = ConfigurationBuilder.build(ClasspathScanner.class.getClassLoader());

        //Special case for JBOSS Application server to get all classes
        try {
            Enumeration<URL> res = ClasspathScanner.class.getClassLoader().getResources("");
            configuration.getUrls().addAll(Collections.list(res));
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

        reflections = new Reflections(configuration);
    }

    public synchronized Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        Assert.requireNonNull(annotation, "annotation");
        return reflections.getTypesAnnotatedWith(annotation);
    }

    public static ClasspathScanner getInstance() {
        return instance;
    }
}
