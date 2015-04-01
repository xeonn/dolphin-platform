package com.canoo.dolphin.server;

import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.ListReferenceModel;
import com.canoo.dolphin.server.util.PrimitiveDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class TestModelCreation extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(1));

        Attribute textAttribute = attributes.get(0);
        assertThat(textAttribute.getPropertyName(), is("text_property"));
        assertThat(textAttribute.getValue(), nullValue());
        assertThat(textAttribute.getBaseValue(), nullValue());
        assertThat(textAttribute.getQualifier(), nullValue());
        assertThat(textAttribute.getTag(), is(Tag.VALUE));
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(1));

        Attribute textAttribute = attributes.get(0);
        assertThat(textAttribute.getPropertyName(), is("text"));
        assertThat(textAttribute.getValue(), nullValue());
        assertThat(textAttribute.getBaseValue(), nullValue());
        assertThat(textAttribute.getQualifier(), nullValue());
        assertThat(textAttribute.getTag(), is(Tag.VALUE));
    }


    @Test
    public void testWithAllPrimitiveDatatypes() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(8));

        for(Attribute attribute : attributes) {
            assertThat(attribute.getValue(), nullValue());
            assertThat(attribute.getBaseValue(), nullValue());
            assertThat(attribute.getQualifier(), nullValue());
            assertThat(attribute.getTag(), is(Tag.VALUE));
        }
    }


    @Test
    public void testWithEnumDataTypesModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getEnumProperty(), notNullValue());
        assertThat(model.getEnumProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(EnumDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(1));


        Attribute textAttribute = attributes.get(0);
        assertThat(textAttribute.getPropertyName(), is("enumProperty"));
        assertThat(textAttribute.getValue(), nullValue());
        assertThat(textAttribute.getBaseValue(), nullValue());
        assertThat(textAttribute.getQualifier(), nullValue());
        assertThat(textAttribute.getTag(), is(Tag.VALUE));
    }

    @Test
    public void testWithSingleReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getReferenceProperty(), notNullValue());
        assertThat(model.getReferenceProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(1));


        Attribute referenceAttribute = attributes.get(0);
        assertThat(referenceAttribute.getPropertyName(), is("referenceProperty"));
        assertThat(referenceAttribute.getValue(), nullValue());
        assertThat(referenceAttribute.getBaseValue(), nullValue());
        assertThat(referenceAttribute.getQualifier(), nullValue());
        assertThat(referenceAttribute.getTag(), is(Tag.VALUE));
    }

    @Test (enabled = false)
    public void testWithListReferenceModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObservableList(), empty());

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, empty());
    }

}
