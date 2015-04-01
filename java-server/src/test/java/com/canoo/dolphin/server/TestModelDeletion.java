package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.ListReferenceModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithEnumDataTypeModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }
    @Test
    public void testWithSingleReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test (enabled = false)
    public void testWithListReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        manager.delete(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(manager.isManaged(model), is(false));
    }

}

