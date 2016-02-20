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

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.server.proxy.TestCarModel;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DolphinUtilsTest extends AbstractDolphinBasedTest {

    @Test
    public void testGetDolphinAttributePropertyNameForMethod() throws Exception {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = createBeanManager(dolphin);

        final BeanInfo info = Introspector.getBeanInfo(TestCarModel.class);
        final String nameForMethod = DolphinUtils.getDolphinAttributeName(info.getPropertyDescriptors()[0]);

        assertThat(nameForMethod, is("brandName"));
    }

}