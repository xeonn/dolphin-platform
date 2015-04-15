package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by hendrikebbers on 15.04.15.
 */
public class ProxyTests extends AbstractDolphinBasedTest {

    @Test
    public void testProxyInstanceCreation() {

        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);

        ModelProxyFactory factory = new ModelProxyFactory(createServerDolphin(),beanRepository);

        TestCarModelInterface myModel = factory.create(TestCarModelInterface.class);
        Assert.assertNotNull(myModel);

        assertEquals("trabant",myModel.getBrandProperty());
    }


}
