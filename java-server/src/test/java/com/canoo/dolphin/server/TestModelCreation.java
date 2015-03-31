package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.PrimitiveDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
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
        assertTrue(manager.isManaged(model));

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
        assertTrue(manager.isManaged(model));

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


    @Test
    public void testWithAllPrimitiveDatatypes() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertNotNull(model);
        assertNotNull(model.getTextProperty());
        assertNull(model.getTextProperty().get());
        assertTrue(manager.isManaged(model));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(1, dolphinModels.size());

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertNotNull(attributes);
        assertEquals(8, attributes.size());

        for(Attribute attribute : attributes) {
            assertEquals(null, attribute.getValue());
            assertEquals(null, attribute.getBaseValue());
            assertEquals(null, attribute.getQualifier());
            assertEquals(Tag.VALUE, attribute.getTag());
        }
    }


    @Test
    public void testWithEnumDataTypesModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        assertNotNull(model);
        assertNotNull(model.getEnumProperty());
        assertNull(model.getEnumProperty().get());
        assertTrue(manager.isManaged(model));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(1, dolphinModels.size());

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertNotNull(attributes);
        assertEquals(1, attributes.size());


        Attribute enumAttribute = attributes.get(0);
        assertEquals("enumProperty", enumAttribute.getPropertyName());
        assertEquals(null, enumAttribute.getValue());
        assertEquals(null, enumAttribute.getBaseValue());
        assertEquals(null, enumAttribute.getQualifier());
        assertEquals(Tag.VALUE, enumAttribute.getTag());

    }

    @Test
    public void testWithSingleReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertNotNull(model);
        assertNotNull(model.getReferenceProperty());
        assertNull(model.getReferenceProperty().get());
        assertTrue(manager.isManaged(model));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertNotNull(dolphinModels);
        assertEquals(1, dolphinModels.size());

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertNotNull(attributes);
        assertEquals(1, attributes.size());


        Attribute referenceAttribute = attributes.get(0);
        assertEquals("referenceProperty", referenceAttribute.getPropertyName());
        assertEquals(null, referenceAttribute.getValue());
        assertEquals(null, referenceAttribute.getBaseValue());
        assertEquals(null, referenceAttribute.getQualifier());
        assertEquals(Tag.VALUE, referenceAttribute.getTag());

    }



}
