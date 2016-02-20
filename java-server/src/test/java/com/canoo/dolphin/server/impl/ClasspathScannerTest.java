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
