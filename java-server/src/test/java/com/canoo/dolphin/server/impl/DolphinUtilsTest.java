package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.proxy.TestCarModelInterface;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DolphinUtilsTest {

    @Test
    public void testGetDolphinAttributePropertyNameForMethod() throws Exception {
        Method method = TestCarModelInterface.class.getDeclaredMethods()[0];
        String nameForMethod = DolphinUtils.getDolphinAttributePropertyNameForMethod(method);

        assertThat(nameForMethod, is("brandName"));
    }
}