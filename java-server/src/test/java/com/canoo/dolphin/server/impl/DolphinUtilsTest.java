package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.proxy.TestCarManufacturer;
import com.canoo.dolphin.server.proxy.TestCarModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import org.testng.annotations.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DolphinUtilsTest {

    @Test
    public void testGetDolphinAttributePropertyNameForMethod() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(TestCarModel.class);
        String nameForMethod = DolphinUtils.getDolphinAttributeName(info.getPropertyDescriptors()[0]);

        assertThat(nameForMethod, is("brandName"));
    }

    @Test
    public void testGetBeanInfoWithHierarchy_interface() throws Exception {

        BeanInfo beanInfo = DolphinUtils.getBeanInfo(TestCarManufacturer.class);

        assertThat(beanInfo.getPropertyDescriptors().length, is(3));

    }

    @Test
    public void testGetBeanInfo_forClass() throws Exception {

        BeanInfo beanInfo = DolphinUtils.getBeanInfo(SimpleAnnotatedTestModel.class);

        assertThat(beanInfo.getPropertyDescriptors().length, is(1));

    }
}