package com.canoo.dolphin.client;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.util.*;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.DolphinConstants;
import mockit.Mocked;
import org.hamcrest.Matchers;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestModelCreation extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType("simple_test_model");
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("text_property")),
                        hasProperty("value", nullValue()),
                        hasProperty("baseValue", nullValue()),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                ),
                allOf(
                        hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                )
        ));

        List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleAnnotatedTestModel.class.getName())),
                                hasProperty("baseValue", is(SimpleAnnotatedTestModel.class.getName())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("text_property")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        )
                ))
        ));
    }

    @Test
    public void testWithSimpleModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SimpleTestModel model = manager.create(SimpleTestModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SimpleTestModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("text")),
                        hasProperty("value", nullValue()),
                        hasProperty("baseValue", nullValue()),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                ),
                allOf(
                        hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                )
        ));

        List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.JAVA_CLASS)),
                                hasProperty("value", is(SimpleTestModel.class.getName())),
                                hasProperty("baseValue", is(SimpleTestModel.class.getName())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("text")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        )
                ))
        ));
    }


    @Test
    public void testWithAllPrimitiveDatatypes(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        PrimitiveDataTypesModel model = manager.create(PrimitiveDataTypesModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getTextProperty(), notNullValue());
        assertThat(model.getTextProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(PrimitiveDataTypesModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();
        assertThat(attributes, hasSize(9));

        for(Attribute attribute : attributes) {
            if (DolphinConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(DolphinConstants.SOURCE_SYSTEM_CLIENT));
                assertThat(attribute.getBaseValue(), Matchers.<Object>is(DolphinConstants.SOURCE_SYSTEM_CLIENT));
            } else {
                assertThat(attribute.getValue(), nullValue());
                assertThat(attribute.getBaseValue(), nullValue());
            }
            assertThat(attribute.getQualifier(), nullValue());
            assertThat(attribute.getTag(), is(Tag.VALUE));
        }

        final List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, hasSize(1));

        final PresentationModel classModel = classModels.get(0);

        final List<Attribute> classAttributes = classModel.getAttributes();
        assertThat(classAttributes, hasSize(10));

        for(Attribute attribute : classAttributes) {
            if (DolphinConstants.JAVA_CLASS.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), is(PrimitiveDataTypesModel.class.getName()));
                assertThat(attribute.getBaseValue(), is(PrimitiveDataTypesModel.class.getName()));
            } else if (DolphinConstants.SOURCE_SYSTEM.equals(attribute.getPropertyName())) {
                assertThat(attribute.getValue(), Matchers.<Object>is(DolphinConstants.SOURCE_SYSTEM_CLIENT));
                assertThat(attribute.getBaseValue(), Matchers.<Object>is(DolphinConstants.SOURCE_SYSTEM_CLIENT));
            } else {
                assertThat(attribute.getValue(), is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal()));
                assertThat(attribute.getBaseValue(), is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal()));
            }
            assertThat(attribute.getQualifier(), nullValue());
            assertThat(attribute.getTag(), is(Tag.VALUE));
        }
    }


    @Test
    public void testWithSingleReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getReferenceProperty(), notNullValue());
        assertThat(model.getReferenceProperty().get(), nullValue());
        assertThat(manager.isManaged(model), is(true));

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(SingleReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("referenceProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("baseValue", nullValue()),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                ),
                allOf(
                        hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                )
        ));

        List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.JAVA_CLASS)),
                                hasProperty("value", is(SingleReferenceModel.class.getName())),
                                hasProperty("baseValue", is(SingleReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("referenceProperty")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        )
                ))
        ));
    }

    @Test
    public void testWithListReferenceModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

        ListReferenceModel model = manager.create(ListReferenceModel.class);

        assertThat(model, notNullValue());
        assertThat(model.getObjectList(), empty());
        assertThat(model.getPrimitiveList(), empty());

        List<ClientPresentationModel> dolphinModels = dolphin.findAllPresentationModelsByType(ListReferenceModel.class.getName());
        assertThat(dolphinModels, hasSize(1));

        PresentationModel dolphinModel = dolphinModels.get(0);

        List<Attribute> attributes = dolphinModel.getAttributes();

        assertThat(attributes, contains(
                allOf(
                        hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                )
        ));

        List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.JAVA_CLASS)),
                                hasProperty("value", is(ListReferenceModel.class.getName())),
                                hasProperty("baseValue", is(ListReferenceModel.class.getName())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("objectList")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("primitiveList")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        )
                ))
        ));
    }

    @Test
    public void testWithInheritedModel(@Mocked HttpClientConnector connector) {
        final ClientDolphin dolphin = createClientDolphin(connector);
        final BeanManager manager = createBeanManager(dolphin);

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

        assertThat(attributes, containsInAnyOrder(
                allOf(
                        hasProperty("propertyName", is("childProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("baseValue", nullValue()),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                ),
                allOf(
                        hasProperty("propertyName", is("parentProperty")),
                        hasProperty("value", nullValue()),
                        hasProperty("baseValue", nullValue()),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                ),
                allOf(
                        hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                        hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                        hasProperty("qualifier", nullValue()),
                        hasProperty("tag", is(Tag.VALUE))
                )
        ));

        List<ClientPresentationModel> classModels = dolphin.findAllPresentationModelsByType(DolphinConstants.DOLPHIN_BEAN);
        assertThat(classModels, hasSize(1));
        assertThat(classModels, contains(
                hasProperty("attributes", containsInAnyOrder(
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.JAVA_CLASS)),
                                hasProperty("value", is(ChildModel.class.getName())),
                                hasProperty("baseValue", is(ChildModel.class.getName())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("childProperty")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is("parentProperty")),
                                hasProperty("value", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("baseValue", is(ClassRepositoryImpl.FieldType.UNKNOWN.ordinal())),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        ),
                        allOf(
                                hasProperty("propertyName", is(DolphinConstants.SOURCE_SYSTEM)),
                                hasProperty("value", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("baseValue", is(DolphinConstants.SOURCE_SYSTEM_CLIENT)),
                                hasProperty("qualifier", nullValue()),
                                hasProperty("tag", is(Tag.VALUE))
                        )
                ))
        ));
    }

}
