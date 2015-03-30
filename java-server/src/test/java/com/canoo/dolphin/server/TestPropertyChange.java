package com.canoo.dolphin.server;

import com.canoo.dolphin.mapping.ValueChangeEvent;
import com.canoo.dolphin.mapping.ValueChangeListener;
import com.canoo.dolphin.server.util.AbstractDolphinBasedTest;
import com.canoo.dolphin.server.util.SimpleAnnotatedTestModel;
import com.canoo.dolphin.server.util.SimpleTestModel;
import org.junit.Test;
import org.opendolphin.core.server.ServerDolphin;

import static org.junit.Assert.assertEquals;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public class TestPropertyChange extends AbstractDolphinBasedTest {

    private String newValue;

    private String oldValue;

    private boolean listenerCalled;

    @Test
    public void testWithAnnotatedSimpleModel() {
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleAnnotatedTestModel model = manager.create(SimpleAnnotatedTestModel.class);

        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertEquals(model.getTextProperty(), evt.getSource());
                newValue = evt.getNewValue();
                oldValue = evt.getOldValue();
                listenerCalled = true;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertEquals(null, newValue);
        assertEquals(null, oldValue);
        assertEquals(false, listenerCalled);

        model.getTextProperty().set("Hallo Property");
        assertEquals("Hallo Property", newValue);
        assertEquals(null, oldValue);
        assertEquals(true, listenerCalled);

        listenerCalled = false;
        model.getTextProperty().set("Hallo Property2");
        assertEquals("Hallo Property2", newValue);
        assertEquals("Hallo Property", oldValue);
        assertEquals(true, listenerCalled);

        listenerCalled = false;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertEquals("Hallo Property2", newValue);
        assertEquals("Hallo Property", oldValue);
        assertEquals(false, listenerCalled);
    }

    @Test
    public void testWithSimpleModel() {
        newValue = null;
        oldValue = null;
        listenerCalled = false;
        ServerDolphin dolphin = createServerDolphin();
        BeanManager manager = new BeanManager(dolphin);

        final SimpleTestModel model = manager.create(SimpleTestModel.class);

        ValueChangeListener<String> myListener = new ValueChangeListener<String>() {
            @Override
            public void valueChanged(ValueChangeEvent<? extends String> evt) {
                assertEquals(model.getTextProperty(), evt.getSource());
                newValue = evt.getNewValue();
                oldValue = evt.getOldValue();
                listenerCalled = true;
            }
        };

        model.getTextProperty().addValueListener(myListener);
        assertEquals(null, newValue);
        assertEquals(null, oldValue);
        assertEquals(false, listenerCalled);

        model.getTextProperty().set("Hallo Property");
        assertEquals("Hallo Property", newValue);
        assertEquals(null, oldValue);
        assertEquals(true, listenerCalled);

        listenerCalled = false;
        model.getTextProperty().set("Hallo Property2");
        assertEquals("Hallo Property2", newValue);
        assertEquals("Hallo Property", oldValue);
        assertEquals(true, listenerCalled);

        listenerCalled = false;
        model.getTextProperty().removeValueListener(myListener);
        model.getTextProperty().set("Hallo Property3");
        assertEquals("Hallo Property2", newValue);
        assertEquals("Hallo Property", oldValue);
        assertEquals(false, listenerCalled);
    }
}
