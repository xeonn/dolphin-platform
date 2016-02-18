package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 18.02.16.
 */
public class ServerPresentationModelBuilderTest extends AbstractDolphinBasedTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        new ServerPresentationModelBuilder(null);
    }

    @Test
    public void testSimpleCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);

        ServerPresentationModel model = builder.create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 1);
        assertEquals(model.getAttributes().get(0).getPropertyName(), PlatformConstants.SOURCE_SYSTEM);
        assertEquals(model.getAttributes().get(0).getValue(), PlatformConstants.SOURCE_SYSTEM_SERVER);
    }

    @Test
    public void testWithAttributeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withAttribute("testName").create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 2);
        assertNotNull(model.getAt(PlatformConstants.SOURCE_SYSTEM));
        assertNotNull(model.getAt("testName"));
    }

    @Test
    public void testWithFilledAttributeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withAttribute("testName", "testValue").create();
        assertNotNull(model);
        assertEquals(model.getAttributes().size(), 2);
        assertNotNull(model.getAt(PlatformConstants.SOURCE_SYSTEM));
        assertNotNull(model.getAt("testName"));
        assertEquals(model.getAt("testName").getValue(), "testValue");
    }

    @Test
    public void testWithIdCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withId("testId").create();
        assertNotNull(model);
        assertEquals(model.getId(), "testId");
    }

    @Test
    public void testWithTypeCreation() {
        ServerDolphin serverDolphin = createServerDolphin();
        ServerPresentationModelBuilder builder = new ServerPresentationModelBuilder(serverDolphin);
        ServerPresentationModel model = builder.withType("testType").create();
        assertNotNull(model);
        assertEquals(model.getPresentationModelType(), "testType");
    }
}
