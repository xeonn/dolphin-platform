package com.canoo.dolphin.server.context;

import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class DefaultOpenDolphinFactoryTest {

    @Test
    public void testDolphinCreation() {
        OpenDolphinFactory factory = new DefaultOpenDolphinFactory();
        DefaultServerDolphin serverDolphin = factory.create();
        assertNotNull(serverDolphin);
        assertNotNull(serverDolphin.getModelStore());
        assertNotNull(serverDolphin.getServerConnector());
        assertNotNull(serverDolphin.getServerModelStore());
        assertNotNull(serverDolphin.getServerConnector().getCodec());
        assertEquals(OptimizedJsonCodec.class, serverDolphin.getServerConnector().getCodec().getClass());

        assertEquals(serverDolphin.getServerConnector().getRegistry().getActions().size(), 8);
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("BaseValueChanged"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("Empty"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("ValueChanged"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("CreatePresentationModel"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("DeletedAllPresentationModelsOfType"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("AttributeCreated"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("ChangeAttributeMetadata"));
        assertTrue(serverDolphin.getServerConnector().getRegistry().getActions().containsKey("DeletedPresentationModel"));

        assertEquals(serverDolphin.listPresentationModelIds().size(), 0);
    }

}
