package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.impl.DefaultJavaFXBindable;
import com.canoo.dolphin.client.javafx.impl.DoubleDolphinBindable;
import com.canoo.dolphin.client.javafx.impl.DoubleJavaFXBidirectionalBindable;
import com.canoo.dolphin.client.javafx.impl.DoubleJavaFXBindable;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableDoubleValue;

/**
 * Method to create JavaFX property wrappers for dolphin platform properties
 * This will be changed in the next version to a fluent API like FXBinder.bind(jfxp).to(dp);
 */
public class FXBinder {

    private FXBinder() {
    }

    public static JavaFXBindable<Number> bind(WritableDoubleValue writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static JavaFXBidirectionalBindable<Number> bind(DoubleProperty property) {
        return new DoubleJavaFXBidirectionalBindable(property);
    }

    public static DolphinBindable<Number> bind(Property<Double> property) {
        return new DoubleDolphinBindable(property);
    }

    public void demo() {
        Property<Double> doubleProperty = null;

        FXBinder.bind(new SimpleDoubleProperty(3)).bidirectionalTo(doubleProperty);
        FXBinder.bind(doubleProperty).bidirectionalTo(new SimpleDoubleProperty());
        FXBinder.bind(doubleProperty).to(new ReadOnlyDoubleWrapper().getReadOnlyProperty());
    }

}
