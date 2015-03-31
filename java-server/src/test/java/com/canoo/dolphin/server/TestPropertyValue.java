package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.PrimitiveDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.junit.Test;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestPropertyValue extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        ServerPresentationModel dolphinModel = dolphin.findAllPresentationModelsByType("simple_test_model").get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("text_property");
        assertEquals(null, textAttribute.getValue());

        model.getTextProperty().set("Hallo Platform");
        assertEquals("Hallo Platform", textAttribute.getValue());
        assertEquals("Hallo Platform", model.getTextProperty().get());

        textAttribute.setValue("Hallo Dolphin");
        assertEquals("Hallo Dolphin", textAttribute.getValue());
        assertEquals("Hallo Dolphin", model.getTextProperty().get());
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        ServerPresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("text");
        assertEquals(null, textAttribute.getValue());

        model.getTextProperty().set("Hallo Platform");
        assertEquals("Hallo Platform", textAttribute.getValue());
        assertEquals("Hallo Platform", model.getTextProperty().get());

        textAttribute.setValue("Hallo Dolphin");
        assertEquals("Hallo Dolphin", textAttribute.getValue());
        assertEquals("Hallo Dolphin", model.getTextProperty().get());
    }

    @Test
    public void testWithAllPrimitiveDataTypesModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        ServerPresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("textProperty");
        assertEquals(null, textAttribute.getValue());

        model.getTextProperty().set("Hallo Platform");
        assertEquals("Hallo Platform", textAttribute.getValue());
        assertEquals("Hallo Platform", model.getTextProperty().get());

        textAttribute.setValue("Hallo Dolphin");
        assertEquals("Hallo Dolphin", textAttribute.getValue());
        assertEquals("Hallo Dolphin", model.getTextProperty().get());


        Attribute intAttribute = dolphinModel.findAttributeByPropertyName("integerProperty");
        assertEquals(null, intAttribute.getValue());

        model.getIntegerProperty().set(1);
        assertEquals(1, intAttribute.getValue());
        assertEquals(1, model.getIntegerProperty().get().intValue());

        intAttribute.setValue(2);
        assertEquals(2, intAttribute.getValue());
        assertEquals(2, model.getIntegerProperty().get().intValue());


        Attribute booleanAttribute = dolphinModel.findAttributeByPropertyName("booleanProperty");
        assertEquals(null, booleanAttribute.getValue());

        model.getBooleanProperty().set(true);
        assertEquals(true, booleanAttribute.getValue());
        assertEquals(true, model.getBooleanProperty().get().booleanValue());

        model.getBooleanProperty().set(false);
        assertEquals(false, booleanAttribute.getValue());
        assertEquals(false, model.getBooleanProperty().get().booleanValue());

    }


    @Test
    public void testWithEnumDataTypeModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        ServerPresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName()).get(0);

        Attribute enumAttribute = dolphinModel.findAttributeByPropertyName("enumProperty");
        assertEquals(null, enumAttribute.getValue());

        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_1);
        assertEquals(EnumDataTypesModel.DataType.VALUE_1.ordinal(), enumAttribute.getValue());
        assertEquals(EnumDataTypesModel.DataType.VALUE_1, model.getEnumProperty().get());

        ServerPresentationModel enumModels = dolphin.findPresentationModelById(EnumDataTypesModel.DataType.class.getName());
        assertNotNull(enumModels);

        enumAttribute.setValue(EnumDataTypesModel.DataType.VALUE_2.ordinal());
        assertEquals(EnumDataTypesModel.DataType.VALUE_2.ordinal(), enumAttribute.getValue());
        assertEquals(EnumDataTypesModel.DataType.VALUE_2, model.getEnumProperty().get());
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = new BeanManager(dolphin);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        ref1.getTextProperty().set("ref1_text");
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        ref2.getTextProperty().set("ref2_text");
        final List<ServerPresentationModel> refPMs = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final ServerPresentationModel ref1PM = "ref1_text".equals(refPMs.get(0).findAttributeByPropertyName("text").getValue())? refPMs.get(0) : refPMs.get(1);
        final ServerPresentationModel ref2PM = "ref2_text".equals(refPMs.get(0).findAttributeByPropertyName("text").getValue())? refPMs.get(0) : refPMs.get(1);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final ServerPresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName()).get(0);

        final Attribute referenceAttribute = dolphinModel.findAttributeByPropertyName("referenceProperty");
        assertEquals(null, referenceAttribute.getValue());

        model.getReferenceProperty().set(ref1);
        assertEquals(ref1PM.getId(), referenceAttribute.getValue());
        assertEquals(ref1, model.getReferenceProperty().get());

        referenceAttribute.setValue(ref2PM.getId());
        assertEquals(ref2PM.getId(), referenceAttribute.getValue());
        assertEquals(ref2, model.getReferenceProperty().get());
    }
}
