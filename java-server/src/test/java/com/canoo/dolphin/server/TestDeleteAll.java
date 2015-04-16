package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

        manager.deleteAll(SimpleTestModel.class);
        assertThat(manager.isManaged(model1), is(false));
        assertThat(manager.isManaged(model1), is(false));
        assertThat(manager.isManaged(model2), is(false));
        assertThat(manager.isManaged(model3), is(false));
        assertThat(manager.isManaged(wrongModel), is(true));
    }
}

