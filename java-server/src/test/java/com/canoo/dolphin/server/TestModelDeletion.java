package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.junit.Test;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertNotNull(dolphinModels);
        assertEquals(0, dolphinModels.size());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertNotNull(allDolphinModels);
        assertEquals(1, allDolphinModels.size()); //Dolphin Class Repository wurde bereits angelegt

        assertFalse(manager.isManaged(model));
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(0, dolphinModels.size());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertNotNull(allDolphinModels);
        assertEquals(1, allDolphinModels.size()); //Dolphin Class Repository wurde bereits angelegt

        assertFalse(manager.isManaged(model));
    }

    @Test
    public void testWithEnumDataTypeModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(0, dolphinModels.size());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertNotNull(allDolphinModels);
        assertEquals(1, allDolphinModels.size()); //Dolphin Class Repository wurde bereits angelegt

        assertFalse(manager.isManaged(model));
    }
    @Test
    public void testWithSingleReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(0, dolphinModels.size());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertNotNull(allDolphinModels);
        assertEquals(1, allDolphinModels.size()); //Dolphin Class Repository wurde bereits angelegt

        assertFalse(manager.isManaged(model));
    }

}

