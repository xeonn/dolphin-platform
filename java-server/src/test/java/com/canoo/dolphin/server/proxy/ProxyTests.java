package com.canoo.dolphin.server.proxy;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ProxyTests {

    @Test
    public void testProxyInstanceCreation() {
        ModelProxyFactory factory = new ModelProxyFactory();
        TestCarModelInterface myModel = factory.create(TestCarModelInterface.class);
        Assert.assertNotNull(myModel);

        assertEquals("trabant",myModel.getBrandProperty());
    }


}
