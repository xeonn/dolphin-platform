package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.*;
import mockit.Mocked;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestPropertyValue extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        PresentationModel dolphinModel = dolphin.findAllPresentationModelsByType("simple_test_model").get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("text_property");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Dolphin"));
        assertThat(model.getTextProperty().get(), is("Hallo Dolphin"));
    }

    @Test
    public void testWithSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        PresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("text");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Dolphin"));
        assertThat(model.getTextProperty().get(), is("Hallo Dolphin"));
    }

    @Test
    public void testWithAllPrimitiveDataTypesModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        PresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName()).get(0);

        Attribute textAttribute = dolphinModel.findAttributeByPropertyName("textProperty");
        assertThat(textAttribute.getValue(), nullValue());

        model.getTextProperty().set("Hallo Platform");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getTextProperty().get(), is("Hallo Platform"));

        textAttribute.setValue("Hallo Dolphin");
        assertThat(textAttribute.getValue(), is((Object) "Hallo Dolphin"));
        assertThat(model.getTextProperty().get(), is("Hallo Dolphin"));


        Attribute intAttribute = dolphinModel.findAttributeByPropertyName("integerProperty");
        assertThat(intAttribute.getValue(), nullValue());

        model.getIntegerProperty().set(1);
        assertThat(intAttribute.getValue(), is((Object) 1));
        assertThat(model.getIntegerProperty().get(), is(1));

        intAttribute.setValue(2);
        assertThat(intAttribute.getValue(), is((Object) 2));
        assertThat(model.getIntegerProperty().get(), is(2));


        Attribute booleanAttribute = dolphinModel.findAttributeByPropertyName("booleanProperty");
        assertThat(booleanAttribute.getValue(), nullValue());

        model.getBooleanProperty().set(true);
        assertThat(booleanAttribute.getValue(), is((Object) true));
        assertThat(model.getBooleanProperty().get(), is(true));

        model.getBooleanProperty().set(false);
        assertThat(booleanAttribute.getValue(), is((Object) false));
        assertThat(model.getBooleanProperty().get(), is(false));

    }


    @Test
    public void testWithSingleReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        ref1.getTextProperty().set("ref1_text");
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        ref2.getTextProperty().set("ref2_text");
        final List<ClientPresentationModel> refPMs = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        final PresentationModel ref1PM = "ref1_text".equals(refPMs.get(0).findAttributeByPropertyName("text").getValue())? refPMs.get(0) : refPMs.get(1);
        final PresentationModel ref2PM = "ref2_text".equals(refPMs.get(0).findAttributeByPropertyName("text").getValue())? refPMs.get(0) : refPMs.get(1);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final PresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName()).get(0);

        final Attribute referenceAttribute = dolphinModel.findAttributeByPropertyName("referenceProperty");
        assertThat(referenceAttribute.getValue(), nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat(referenceAttribute.getValue(), is((Object) ref1PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref1));

        referenceAttribute.setValue(ref2PM.getId());
        assertThat(referenceAttribute.getValue(), is((Object) ref2PM.getId()));
        assertThat(model.getReferenceProperty().get(), is(ref2));
    }

    @Test
    public void testWithInheritedModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ChildModel model = manager.create(ChildModel.class);

        PresentationModel dolphinModel = dolphin.findAllPresentationModelsByType(ChildModel.class.getName()).get(0);

        Attribute childAttribute = dolphinModel.findAttributeByPropertyName("childProperty");
        assertThat(childAttribute.getValue(), nullValue());
        Attribute parentAttribute = dolphinModel.findAttributeByPropertyName("parentProperty");
        assertThat(parentAttribute.getValue(), nullValue());

        model.getChildProperty().set("Hallo Platform");
        assertThat(childAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), nullValue());
        assertThat(model.getParentProperty().get(), nullValue());

        parentAttribute.setValue("Hallo Dolphin");
        assertThat(childAttribute.getValue(), is((Object) "Hallo Platform"));
        assertThat(model.getChildProperty().get(), is("Hallo Platform"));
        assertThat(parentAttribute.getValue(), is((Object) "Hallo Dolphin"));
        assertThat(model.getParentProperty().get(), is("Hallo Dolphin"));
    }


}
