package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.impl.*;
import com.canoo.dolphin.client.javafx.impl.numeric.*;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.beans.value.*;

/**
 * Method to create JavaFX property wrappers for dolphin platform properties
 * This will be changed in the next version to a fluent API like FXBinder.bind(jfxp).to(dp);
 */
public class FXBinder {

    private FXBinder() {
    }

    public static JavaFXBindable<Double> bind(WritableDoubleValue writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static JavaFXBindable<Float> bind(WritableFloatValue writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static JavaFXBindable<Integer> bind(WritableIntegerValue writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static JavaFXBindable<Long> bind(WritableLongValue writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static NumericJavaFXBidirectionaBindable<Double> bind(DoubleProperty property) {
        return new DoubleJavaFXBidirectionalBindable(property);
    }

    public static NumericJavaFXBidirectionaBindable<Float> bind(FloatProperty property) {
        return new FloatJavaFXBidirectionalBindable(property);
    }

    public static NumericJavaFXBidirectionaBindable<Integer> bind(IntegerProperty property) {
        return new IntegerJavaFXBidirectionalBindable(property);
    }

    public static NumericJavaFXBidirectionaBindable<Long> bind(LongProperty property) {
        return new LongJavaFXBidirectionalBindable(property);
    }

    public static <T> JavaFXBindable<T> bind(WritableValue<T> writableDoubleValue) {
        return new DefaultJavaFXBindable(writableDoubleValue);
    }

    public static <T> JavaFXBidirectionalBindable<T> bind(javafx.beans.property.Property<T> property) {
        return new DefaultJavaFXBidirectionalBindable<>(property);
    }

    public static <T> DolphinBindable<T> bind(Property<T> property) {
        return new DefaultDolphinBindable<>(property);
    }

    public static NumericDolphinBindable<Double> bindDouble(Property<Double> property) {
        return new DoubleDolphinBindable(property);
    }

    public static NumericDolphinBindable<Float> bindFloat(Property<Float> property) {
        return new FloatDolphinBindable(property);
    }

    public static NumericDolphinBindable<Integer> bindInteger(Property<Integer> property) {
        return new IntegerDolphinBindable(property);
    }

    public static NumericDolphinBindable<Long> bindLong(Property<Long> property) {
        return new LongDolphinBindable(property);
    }
}
