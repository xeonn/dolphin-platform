package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertNotNull(dolphinModels);
        assertEquals(0, dolphinModels.size());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertNotNull(allDolphinModels);
        assertEquals(0, allDolphinModels.size());

        //TODO: Hier wär es noch super, wenn man das über API überprüfen könnte. z.B. manager.isManaged(model);
    }

}

