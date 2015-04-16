package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ProxyTests extends AbstractDolphinBasedTest {

    private TestCarModelInterface myModel;

    @BeforeMethod
    public void setUp() throws Exception {
        ServerDolphin dolphin = createServerDolphin();
        ClassRepository classRepository = new ClassRepository(dolphin);
        BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);

        ModelProxyFactory factory = new ModelProxyFactory(dolphin,beanRepository);

        myModel = factory.create(TestCarModelInterface.class);

    }

    @Test
    public void testProxyInstanceCreation_not_initialized() {
        assertNotNull(myModel);

        assertEquals(null, myModel.getBrandNameProperty().get());
    }

    @Test
    public void testProxyInstanceCreation() {
        assertNotNull(myModel);

        myModel.getBrandNameProperty().set("a String");
        assertEquals("a String", myModel.getBrandNameProperty().get());
    }




}
