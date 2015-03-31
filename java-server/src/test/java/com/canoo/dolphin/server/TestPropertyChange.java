package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.EnumDataTypesModel;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.server.ServerDolphin;

import static org.junit.Assert.assertEquals;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestPropertyChange extends AbstractDolphinBasedTest {

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        final ListerResults results = new ListerResults();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertEquals(model.getTextProperty(), evt.getSource());
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalled = true;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertEquals(null, results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(false, results.listenerCalled);

        model.getTextProperty().set("Hallo Property");
        assertEquals("Hallo Property", results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getTextProperty().set("Hallo Property2");
        assertEquals("Hallo Property2", results.newValue);
        assertEquals("Hallo Property", results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertEquals("Hallo Property2", results.newValue);
        assertEquals("Hallo Property", results.oldValue);
        assertEquals(false, results.listenerCalled);
    }

    @Test
    public void testWithSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleTestModel model = manager.create(SimpleTestModel.class);

        final ListerResults results = new ListerResults();
        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertEquals(model.getTextProperty(), evt.getSource());
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalled = true;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertEquals(null, results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(false, results.listenerCalled);

        model.getTextProperty().set("Hallo Property");
        assertEquals("Hallo Property", results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getTextProperty().set("Hallo Property2");
        assertEquals("Hallo Property2", results.newValue);
        assertEquals("Hallo Property", results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertEquals("Hallo Property2", results.newValue);
        assertEquals("Hallo Property", results.oldValue);
        assertEquals(false, results.listenerCalled);
    }


    @Test
    public void testWithEnumDataTypeModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final EnumDataTypesModel model = manager.create(EnumDataTypesModel.class);

        final ListerResults results = new ListerResults();
        ValueChangeListener<EnumDataTypesModel.DataType> myListener = new ValueChangeListener<EnumDataTypesModel.DataType>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends EnumDataTypesModel.DataType> evt) {
                assertEquals(model.getEnumProperty(), evt.getSource());
                results.newValue = evt.getNewValue();
                results.oldValue = evt.getOldValue();
                results.listenerCalled = true;
            }
        };

        model.getEnumProperty().addValueListener(myListener);
        assertEquals(null, results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(false, results.listenerCalled);

        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_1);
        assertEquals(EnumDataTypesModel.DataType.VALUE_1, results.newValue);
        assertEquals(null, results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_2);
        assertEquals(EnumDataTypesModel.DataType.VALUE_2, results.newValue);
        assertEquals(EnumDataTypesModel.DataType.VALUE_1, results.oldValue);
        assertEquals(true, results.listenerCalled);

        results.listenerCalled = false;
        model.getEnumProperty().removeValueListener(myListener);
        model.getEnumProperty().set(EnumDataTypesModel.DataType.VALUE_3);
        assertEquals(EnumDataTypesModel.DataType.VALUE_2, results.newValue);
        assertEquals(EnumDataTypesModel.DataType.VALUE_1, results.oldValue);
        assertEquals(false, results.listenerCalled);
    }

    private static class ListerResults {
        public Object newValue;
        public Object oldValue;
        public boolean listenerCalled;
    }
}
