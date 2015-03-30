package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.server.ServerDolphin;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hendrikebbers on 31.03.15.
 */
public class QueryTest extends AbstractDolphinBasedTest {

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model1 = manager.create(SimpleTestModel.class);
        model1.getTextProperty().set("Hallo");
        SimpleTestModel model2 = manager.create(SimpleTestModel.class);
        SimpleTestModel model3 = manager.create(SimpleTestModel.class);


        try {
            List<SimpleTestModel> result = manager.createQuery(SimpleTestModel.class).withNotNull("text").run();
            assertEquals(1, result.size());
            assertTrue(result.contains(model1));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            List<SimpleTestModel> result = manager.createQuery(SimpleTestModel.class).withNull("text").run();
            assertEquals(2, result.size());
            assertTrue(result.contains(model2));
            assertTrue(result.contains(model3));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            List<SimpleTestModel> result = manager.createQuery(SimpleTestModel.class).withNotEquals("text", "Hallo").run();
            assertEquals(2, result.size());
            assertTrue(result.contains(model2));
            assertTrue(result.contains(model3));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            List<SimpleTestModel> result = manager.createQuery(SimpleTestModel.class).withNotEquals("text", "unknown").run();
            assertEquals(3, result.size());
            assertTrue(result.contains(model1));
            assertTrue(result.contains(model2));
            assertTrue(result.contains(model3));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            List<SimpleTestModel> result = manager.createQuery(SimpleTestModel.class).withNotEquals("text", "unknown").withNotNull("text").run();
            assertEquals(1, result.size());
            assertTrue(result.contains(model1));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}


