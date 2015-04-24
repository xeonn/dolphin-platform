package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
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
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithEnumDataTypeModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }
    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));

        assertThat(manager.isManaged(model), is(false));
    }

    @Test
    public void testWithListReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        manager.remove(model);

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, empty());

        Collection<ServerPresentationModel> allDolphinModels = dolphin.listPresentationModels();
        assertThat(allDolphinModels, hasSize(1));    //Dolphin Class Repository wurde bereits angelegt

        assertThat(manager.isManaged(model), is(false));
    }

}

