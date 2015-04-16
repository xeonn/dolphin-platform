package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestFindAll extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final ClassRepository classRepository = new ClassRepository(dolphin);
        final BeanRepository beanRepository = new BeanRepository(dolphin, classRepository);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);

        manager.create(SimpleAnnotatedTestModel.class);

        List<SimpleTestModel> models = manager.findAll(SimpleTestModel.class);
        assertThat(models, is(Arrays.asList(model1, model2, model3)));
    }
}
