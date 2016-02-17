package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.impl.util.AnnotatedClassForClasspathScan;
import com.canoo.dolphin.server.impl.util.AnnotationForClasspathScanTest;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by hendrikebbers on 17.02.16.
 */
public class ClasspathScannerTest {

    @Test
    public void testSimpleScan() {
        //There can't be a class that is annotated with Inject
        Set<Class<?>> classes = ClasspathScanner.getInstance().getTypesAnnotatedWith(Inject.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 0);

        classes = ClasspathScanner.getInstance().getTypesAnnotatedWith(AnnotationForClasspathScanTest.class);
        assertNotNull(classes);
        assertEquals(classes.size(), 1);
        assertTrue(classes.contains(AnnotatedClassForClasspathScan.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        Set<Class<?>> classes = ClasspathScanner.getInstance().getTypesAnnotatedWith(null);
    }

}
