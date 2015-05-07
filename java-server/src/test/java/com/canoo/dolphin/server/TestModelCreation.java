package com.canoo.dolphin.server;

import com.canoo.dolphin.server.impl.BeanBuilder;
import com.canoo.dolphin.server.impl.BeanManagerImpl;
import com.canoo.dolphin.server.impl.BeanRepository;
import com.canoo.dolphin.server.impl.ClassRepository;
import com.canoo.dolphin.server.impl.collections.ListMapper;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.ChildModel;
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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class TestModelCreation extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

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
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

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
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

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
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

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

    @Test
    public void testWithListReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObjectList(), empty());
        assertThat(model.getPrimitiveList(), empty());

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, empty());
    }

    @Test
    public void testWithInheritedModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanRepository beanRepository = new BeanRepository(dolphin);
        final ClassRepository classRepository = new ClassRepository(dolphin, beanRepository);
        final ListMapper listMapper = new ListMapper(dolphin, classRepository, beanRepository);
        final BeanBuilder beanBuilder = new BeanBuilder(dolphin, classRepository, beanRepository, listMapper);
        final BeanManagerImpl manager = new BeanManagerImpl(beanRepository, beanBuilder);

        ChildModel model = manager.create(ChildModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getParentProperty(), notNullValue());
        assertThat(model.getParentProperty().get(), nullValue());
        assertThat(model.getChildProperty(), notNullValue());
        assertThat(model.getChildProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ServerPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ChildModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        ServerPresentationModel dolphinModel = dolphinModels.get(0);

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
