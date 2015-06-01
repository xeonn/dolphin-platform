package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.server.util.DataType;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;


public class ReflectionHelperTest {

    @Test
    public void testIsEnumType() throws Exception {

        assertTrue(ReflectionHelper.isEnumType(DataType.class));
    }
}