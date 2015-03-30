package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestModelCreation extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertNotNull(model);
        assertNotNull(model.getTextProperty());
        assertNull(model.getTextProperty().get());

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertNotNull(dolphinModels);
        assertEquals(1, dolphinModels.size());

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertNotNull(attributes);
        assertEquals(1, attributes.size());


        Attribute textAttribute = attributes.get(0);
        assertEquals("text_property", textAttribute.getPropertyName());
        assertEquals(null, textAttribute.getValue());
        assertEquals(null, textAttribute.getBaseValue());
        assertEquals(null, textAttribute.getQualifier());
        assertEquals(Tag.VALUE, textAttribute.getTag());

    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertNotNull(model);
        assertNotNull(model.getTextProperty());
        assertNull(model.getTextProperty().get());

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(1, dolphinModels.size());

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertNotNull(attributes);
        assertEquals(1, attributes.size());


        Attribute textAttribute = attributes.get(0);
        assertEquals("text", textAttribute.getPropertyName());
        assertEquals(null, textAttribute.getValue());
        assertEquals(null, textAttribute.getBaseValue());
        assertEquals(null, textAttribute.getQualifier());
        assertEquals(Tag.VALUE, textAttribute.getTag());

    }

}
