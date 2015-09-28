package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.impl.*;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableStringValue;
import javafx.beans.value.WritableValue;

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

    public static NumericJavaFXBidirectionaBindable<Double> bind(DoubleProperty property) {
        return new DoubleJavaFXBidirectionalBindable(property);
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

    public static NumericDolphinBindable<Double> bindNumeric(Property<Double> property) {
        return new DoubleDolphinBindable(property);
    }

    public void demo() {
        Property<Double> doubleDolphinProperty = null;
        Property<Boolean> booleanDolphinProperty = null;
        Property<String> stringDolphinProperty = null;

        DoubleProperty doubleJavaFXProperty = null;
        BooleanProperty booleanJavaFXProperty = null;
        StringProperty stringJavaFXProperty = null;

        WritableDoubleValue writableDoubleValue = null;
        WritableBooleanValue writableBooleanValue = null;
        WritableStringValue writableStringValue = null;

        ReadOnlyDoubleProperty readOnlyDoubleProperty = null;
        ReadOnlyBooleanProperty readOnlyBooleanProperty = null;
        ReadOnlyStringProperty readOnlyStringProperty = null;

        Converter<Double, String> doubleStringConverter = null;
        Converter<String, Double> stringDoubleConverter = null;
        Converter<Boolean, String> booleanStringConverter = null;
        Converter<String, Boolean> stringBooleanConverter = null;
        BidirectionalConverter<Double, String> doubleStringBidirectionalConverter = null;
        BidirectionalConverter<Boolean, String> booleanStringBidirectionalConverter = null;


        FXBinder.bind(doubleJavaFXProperty).to(doubleDolphinProperty);
        FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, doubleStringBidirectionalConverter.invert());
        FXBinder.bind(writableDoubleValue).to(doubleDolphinProperty);
        FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, stringDoubleConverter);
        FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, doubleStringBidirectionalConverter.invert());

        FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(doubleDolphinProperty);
        FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(stringDolphinProperty, doubleStringBidirectionalConverter.invert());

        FXBinder.bindNumeric(doubleDolphinProperty).toNumeric(doubleJavaFXProperty);
        FXBinder.bindNumeric(doubleDolphinProperty).toNumeric(readOnlyDoubleProperty);
        FXBinder.bind(doubleDolphinProperty).to(stringJavaFXProperty, stringDoubleConverter);
        FXBinder.bind(doubleDolphinProperty).to(readOnlyStringProperty, stringDoubleConverter);
        FXBinder.bind(doubleDolphinProperty).to(stringJavaFXProperty, doubleStringBidirectionalConverter.invert());
        FXBinder.bind(doubleDolphinProperty).to(readOnlyStringProperty, doubleStringBidirectionalConverter.invert());

        FXBinder.bind(doubleDolphinProperty).bidirectionalTo(stringJavaFXProperty, doubleStringBidirectionalConverter.invert());
        FXBinder.bindNumeric(doubleDolphinProperty).bidirectionalToNumeric(doubleJavaFXProperty);

        FXBinder.bind(booleanJavaFXProperty).to(booleanDolphinProperty);
        FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, stringBooleanConverter);
        FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        FXBinder.bind(writableBooleanValue).to(booleanDolphinProperty);
        FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, stringBooleanConverter);
        FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, booleanStringBidirectionalConverter.invert());

        FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(booleanDolphinProperty);
        FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(stringDolphinProperty, booleanStringBidirectionalConverter.invert());

        FXBinder.bind(booleanDolphinProperty).to(booleanJavaFXProperty);
        FXBinder.bind(booleanDolphinProperty).to(readOnlyBooleanProperty);
        FXBinder.bind(booleanDolphinProperty).to(stringJavaFXProperty, stringBooleanConverter);
        FXBinder.bind(booleanDolphinProperty).to(readOnlyStringProperty, stringBooleanConverter);
        FXBinder.bind(booleanDolphinProperty).to(stringJavaFXProperty, booleanStringBidirectionalConverter.invert());
        FXBinder.bind(booleanDolphinProperty).to(readOnlyStringProperty, booleanStringBidirectionalConverter.invert());

        FXBinder.bind(booleanDolphinProperty).bidirectionalTo(stringJavaFXProperty, booleanStringBidirectionalConverter.invert());
        FXBinder.bind(booleanDolphinProperty).bidirectionalTo(booleanJavaFXProperty);

        FXBinder.bind(stringJavaFXProperty).to(stringDolphinProperty);
        FXBinder.bind(stringJavaFXProperty).to(booleanDolphinProperty, booleanStringConverter);
        FXBinder.bind(stringJavaFXProperty).to(booleanDolphinProperty, booleanStringBidirectionalConverter);
        FXBinder.bind(writableStringValue).to(stringDolphinProperty);
        FXBinder.bind(writableStringValue).to(booleanDolphinProperty, booleanStringConverter);
        FXBinder.bind(writableStringValue).to(booleanDolphinProperty, booleanStringBidirectionalConverter);

        FXBinder.bind(stringJavaFXProperty).bidirectionalTo(stringDolphinProperty);
        FXBinder.bind(stringJavaFXProperty).bidirectionalTo(booleanDolphinProperty, booleanStringBidirectionalConverter);

        FXBinder.bind(stringDolphinProperty).to(stringJavaFXProperty);
        FXBinder.bind(stringDolphinProperty).to(readOnlyStringProperty);
        FXBinder.bind(stringDolphinProperty).to(booleanJavaFXProperty, booleanStringConverter);
        FXBinder.bind(stringDolphinProperty).to(readOnlyBooleanProperty, booleanStringConverter);
        FXBinder.bind(stringDolphinProperty).to(booleanJavaFXProperty, booleanStringBidirectionalConverter);
        FXBinder.bind(stringDolphinProperty).to(readOnlyBooleanProperty, booleanStringBidirectionalConverter);

        FXBinder.bind(stringDolphinProperty).bidirectionalTo(booleanJavaFXProperty, booleanStringBidirectionalConverter);
        FXBinder.bind(stringDolphinProperty).bidirectionalTo(stringJavaFXProperty);
    }

}
