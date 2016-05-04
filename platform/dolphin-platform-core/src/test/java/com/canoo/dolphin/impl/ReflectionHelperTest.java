/**
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
package com.canoo.dolphin.impl;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;


public class ReflectionHelperTest {

    @Test
    public void testIsEnumType() throws Exception {
        assertTrue(ReflectionHelper.isEnumType(DataType.class));
    }

    @Test
    public void testBasicType() throws Exception {
        assertTrue(ReflectionHelper.isBasicType(String.class));
        assertTrue(ReflectionHelper.isBasicType(Number.class));
        assertTrue(ReflectionHelper.isBasicType(Long.class));
        assertTrue(ReflectionHelper.isBasicType(Integer.class));
        assertTrue(ReflectionHelper.isBasicType(Double.class));
        assertTrue(ReflectionHelper.isBasicType(Boolean.class));
        assertTrue(ReflectionHelper.isBasicType(Byte.class));
        assertTrue(ReflectionHelper.isBasicType(Short.class));
        assertTrue(ReflectionHelper.isBasicType(BigDecimal.class));
        assertTrue(ReflectionHelper.isBasicType(BigInteger.class));
        assertTrue(ReflectionHelper.isBasicType(Long.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Integer.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Double.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Boolean.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Byte.TYPE));
        assertTrue(ReflectionHelper.isBasicType(Short.TYPE));

        assertFalse(ReflectionHelper.isBasicType(ReflectionHelperTest.class));
        assertFalse(ReflectionHelper.isBasicType(DataType.class));
        assertFalse(ReflectionHelper.isBasicType(UUID.class));
    }
}