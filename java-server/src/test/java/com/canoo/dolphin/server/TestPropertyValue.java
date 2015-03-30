package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import org.junit.Test;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

import static org.junit.Assert.assertEquals;

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
}
