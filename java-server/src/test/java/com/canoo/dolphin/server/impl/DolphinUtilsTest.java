package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.proxy.TestCarModelInterface;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class DolphinUtilsTest {

    @Test
    public void testGetDolphinAttributePropertyNameForMethod() throws Exception {

        DolphinUtils.getDolphinAttributePropertyNameForMethod(TestCarModelInterface.class.getDeclaredMethods()[0]);
    }
}