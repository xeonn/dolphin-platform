package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.*;
import mockit.Mocked;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestModelDeletion extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSingleReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithInheritedModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ChildModel model = manager.create(ChildModel.class);

        manager.remove(model);

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ClientPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }


}

