package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.PrimitiveDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import static org.junit.Assert.assertEquals;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class PropertyValueTest extends AbstractDolphinBasedTest {

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
}
