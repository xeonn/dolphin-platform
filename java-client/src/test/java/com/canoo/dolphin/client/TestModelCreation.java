package com.canoo.dolphin.client;

import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.client.util.ChildModel;
import com.canoo.dolphin.client.util.ListReferenceModel;
import com.canoo.dolphin.client.util.PrimitiveDataTypesModel;
import com.canoo.dolphin.client.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.client.util.SimpleTestModel;
import com.canoo.dolphin.client.util.SingleReferenceModel;
import com.canoo.dolphin.impl.BeanBuilder;
import com.canoo.dolphin.impl.BeanManagerImpl;
import com.canoo.dolphin.impl.BeanRepository;
import com.canoo.dolphin.impl.ClassRepository;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.collections.ListMapper;
import mockit.Mocked;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class TestModelCreation {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

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
    public void testWithSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

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
    public void testWithAllPrimitiveDatatypes(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

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
    public void testWithSingleReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getReferenceProperty(), notNullValue());
        assertThat(model.getReferenceProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(1));


        Attribute referenceAttribute = attributes.get(0);
        assertThat(referenceAttribute.getPropertyName(), is("referenceProperty"));
        assertThat(referenceAttribute.getValue(), nullValue());
        assertThat(referenceAttribute.getBaseValue(), nullValue());
        assertThat(referenceAttribute.getQualifier(), nullValue());
        assertThat(referenceAttribute.getTag(), is(Tag.VALUE));
    }

    @Test
    public void testWithListReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObjectList(), empty());
        assertThat(model.getPrimitiveList(), empty());

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, empty());
    }

    @Test
    public void testWithInheritedModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        dolphin.setClientConnector(connector);
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository, builderFactory);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository, builderFactory);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper, builderFactory);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        ChildModel model = manager.create(ChildModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getParentProperty(), notNullValue());
        assertThat(model.getParentProperty().get(), nullValue());
        assertThat(model.getChildProperty(), notNullValue());
        assertThat(model.getChildProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(2));

        for(Attribute attribute : attributes) {
            assertThat(attribute.getPropertyName(), anyOf(is("childProperty"), is("parentProperty")));
            assertThat(attribute.getValue(), nullValue());
            assertThat(attribute.getBaseValue(), nullValue());
            assertThat(attribute.getQualifier(), nullValue());
            assertThat(attribute.getTag(), is(Tag.VALUE));
        }
    }

}
