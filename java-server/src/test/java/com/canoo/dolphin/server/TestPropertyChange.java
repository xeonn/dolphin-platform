package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.Property;
import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import com.canoo.dolphin.server.util.SingleReferenceModel;
import org.opendolphin.core.server.ServerDolphin;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TestPropertyChange extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        final ListerResults<String> results = new ListerResults<>();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>)evt.getSource(), is(model.getTextProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getTextProperty().set("Hallo Property");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property"));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getTextProperty().set("Hallo Property2");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));

        results.listenerCalls = 0;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleTestModel model = manager.create(SimpleTestModel.class);

        final ListerResults<String> results = new ListerResults<>();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertThat((Property<String>) evt.getSource(), is(model.getTextProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getTextProperty().set("Hallo Property");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property"));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getTextProperty().set("Hallo Property2");
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));

        results.listenerCalls = 0;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is("Hallo Property2"));
        assertThat(results.oldValue, is("Hallo Property"));
    }


    @Test
    public void testWithEnumDataTypeModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = new BeanManager(dolphin);

        final EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        final ListerResults<EnumDataTypesModel.DataType> results = new ListerResults<>();
        final ValueChangeListener<EnumDataTypesModel.DataType> myListener = new ValueChangeListener<EnumDataTypesModel.DataType>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends EnumDataTypesModel.DataType> evt) {
                assertThat((Property<EnumDataTypesModel.DataType>) evt.getSource(), is(model.getEnumProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        model.getEnumProperty().addValueListener(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_1);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(EnumDataTypesModel.DataType.VALUE_1));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_2);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(EnumDataTypesModel.DataType.VALUE_2));
        assertThat(results.oldValue, is(EnumDataTypesModel.DataType.VALUE_1));

        results.listenerCalls = 0;
        model.getEnumProperty().removeValueListener(myListener);
        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_3);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is(EnumDataTypesModel.DataType.VALUE_2));
        assertThat(results.oldValue, is(EnumDataTypesModel.DataType.VALUE_1));
    }


    @Test
    public void testWithSingleReferenceModel() {
        final ServerDolphin dolphin = createServerDolphin();
        final BeanManager manager = new BeanManager(dolphin);

        final SimpleTestModel ref1 = manager.create(SimpleTestModel.class);
        final SimpleTestModel ref2 = manager.create(SimpleTestModel.class);
        final SimpleTestModel ref3 = manager.create(SimpleTestModel.class);

        final SingleReferenceModel model = manager.create(SingleReferenceModel.class);

        final ListerResults<SimpleTestModel> results = new ListerResults<>();
        final ValueChangeListener<SimpleTestModel> myListener = new ValueChangeListener<SimpleTestModel>() {
            @SuppressWarnings("unchecked")
            @Override
            public void valueChanged(ValueChangeEvent<? extends SimpleTestModel> evt) {
                assertThat((Property<SimpleTestModel>) evt.getSource(), is(model.getReferenceProperty()));
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalls++;
            }
        };

        model.getReferenceProperty().addValueListener(myListener);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, nullValue());
        assertThat(results.oldValue, nullValue());

        model.getReferenceProperty().set(ref1);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(ref1));
        assertThat(results.oldValue, nullValue());

        results.listenerCalls = 0;
        model.getReferenceProperty().set(ref2);
        assertThat(results.listenerCalls, is(1));
        assertThat(results.newValue, is(ref2));
        assertThat(results.oldValue, is(ref1));

        results.listenerCalls = 0;
        model.getReferenceProperty().removeValueListener(myListener);
        model.getReferenceProperty().set(ref3);
        assertThat(results.listenerCalls, is(0));
        assertThat(results.newValue, is(ref2));
        assertThat(results.oldValue, is(ref1));
    }

    private static class ListerResults<T> {
        public T newValue;
        public T oldValue;
        public int listenerCalls;
    }
}
