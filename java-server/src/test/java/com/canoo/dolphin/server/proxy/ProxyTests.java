package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ProxyTests extends AbstractDolphinBasedTest {

    private TestCarModelInterface myModel;
    private ModelProxyFactory factory;

    @BeforeMethod
    public void setUp() throws Exception {
        ServerDolphin dolphin = createServerDolphin();
        ClassRepository classRepository = new ClassRepository(dolphin);
        BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);

        factory = new ModelProxyFactory(dolphin, beanRepository);

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

    @Test
    public void proxyInstanceWithSetter() {
        assertNotNull(myModel);

        myModel.setYear(2015);

        assertThat(myModel.getYear(), is(2015));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = "Getter for property brandName should end with \"Property\"")
    public void testInvalidPropertyName() throws Exception {
        factory.create(TestInvalidPropertyInterface.class);
    }
}
