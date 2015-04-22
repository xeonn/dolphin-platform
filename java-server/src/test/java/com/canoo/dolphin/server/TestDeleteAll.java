package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.testng.AssertJUnit.assertTrue;

public class TestDeleteAll extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);


        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        SimpleAnnotatedTestModel wrongModel = manager.create(SimpleAnnotatedTestModel.class);

        manager.detachAll(SimpleTestModel.class);
        assertThat(manager.isManaged(model1), is(false));
        assertThat(manager.isManaged(model2), is(false));
        assertThat(manager.isManaged(model3), is(false));
        assertThat(manager.isManaged(wrongModel), is(true));

        List<ServerPresentationModel> testModels = dolphin.findAllPresentationModelsByType("com.canoo.dolphin.server.util.SimpleTestModel");
        assertThat(testModels, hasSize(0));

    }
}

