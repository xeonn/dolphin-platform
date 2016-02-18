package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.internal.PresentationModelBuilder;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 18.02.16.
 */
public class ServerPresentationModelBuilderFactoryTest extends AbstractDolphinBasedTest {

    @Test
    public void testSimpleCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilderFactory factory = new ServerPresentationModelBuilderFactory(serverDolphin);
        PresentationModelBuilder<ServerPresentationModel> builder = factory.createBuilder();
        assertNotNull(builder);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        new ServerPresentationModelBuilderFactory(null);
    }
}
