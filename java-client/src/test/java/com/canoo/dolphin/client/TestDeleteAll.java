package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import com.canoo.dolphin.impl.BeanBuilder;
import com.canoo.dolphin.impl.BeanRepository;
import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapper;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class TestDeleteAll {

    @Test
    public void testWithSimpleModel() {
        final ClientDolphin dolphin = new ClientDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManager manager = new BeanManager(beanRepository, beanBuilder);


        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        manager.removeAll(SimpleTestModel.class);
        assertThat(manager.isManaged(model1), is(false));
        assertThat(manager.isManaged(model2), is(false));
        assertThat(manager.isManaged(model3), is(false));
        assertThat(manager.isManaged(wrongModel), is(true));

        List<PresentationModel> testModels = dolphin.findAllPresentationModelsByType("com.canoo.dolphin.client.util.SimpleTestModel");
        assertThat(testModels, hasSize(0));

    }
}

