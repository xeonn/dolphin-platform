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

    public static void main(String... args) {
        Property<Double> doubleDolphinProperty = new PropertyImpl<Double>();
        Property<Number> numberDolphinProperty = new PropertyImpl<Number>();
        Property<Boolean> booleanDolphinProperty = new PropertyImpl<Boolean>();
        Property<String> stringDolphinProperty = new PropertyImpl<String>();

        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();

        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();
        WritableStringValue writableStringValue = new SimpleStringProperty();

        ReadOnlyDoubleProperty readOnlyDoubleProperty = new ReadOnlyDoubleWrapper().getReadOnlyProperty();
        ReadOnlyBooleanProperty readOnlyBooleanProperty = new ReadOnlyBooleanWrapper().getReadOnlyProperty();
        ReadOnlyStringProperty readOnlyStringProperty = new ReadOnlyStringWrapper().getReadOnlyProperty();

        Converter<Double, String> doubleStringConverter = d -> d == null ? null : d.toString();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);
        Converter<Boolean, String> booleanStringConverter = b -> b == null ? null : b.toString();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);
        BidirectionalConverter<Double, String> doubleStringBidirectionalConverter = new DefaultBidirectionalConverter<>(doubleStringConverter, stringDoubleConverter);
        BidirectionalConverter<Boolean, String> booleanStringBidirectionalConverter = new DefaultBidirectionalConverter<>(booleanStringConverter, stringBooleanConverter);

        Binding binding = null;
        binding = FXBinder.bind(doubleJavaFXProperty).to(doubleDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(doubleJavaFXProperty).to(numberDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        binding.unbind();
        binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bind(writableDoubleValue).to(doubleDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, stringDoubleConverter);
        binding.unbind();
        binding = FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(doubleDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(stringDolphinProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bindNumeric(doubleDolphinProperty).toNumeric(doubleJavaFXProperty);
        binding.unbind();
        binding = FXBinder.bindNumeric(doubleDolphinProperty).toNumeric(readOnlyDoubleProperty);
        binding.unbind();
        binding = FXBinder.bind(doubleDolphinProperty).to(stringJavaFXProperty, stringDoubleConverter);
        binding.unbind();
        binding = FXBinder.bind(doubleDolphinProperty).to(readOnlyStringProperty, stringDoubleConverter);
        binding.unbind();
        binding = FXBinder.bind(doubleDolphinProperty).to(stringJavaFXProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bind(doubleDolphinProperty).to(readOnlyStringProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bind(doubleDolphinProperty).bidirectionalTo(stringJavaFXProperty, doubleStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bindNumeric(doubleDolphinProperty).bidirectionalToNumeric(doubleJavaFXProperty);
        binding.unbind();

        binding = FXBinder.bind(booleanJavaFXProperty).to(booleanDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, stringBooleanConverter);
        binding.unbind();
        binding = FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bind(writableBooleanValue).to(booleanDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, stringBooleanConverter);
        binding.unbind();
        binding = FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(booleanDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bind(booleanDolphinProperty).to(booleanJavaFXProperty);
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).to(readOnlyBooleanProperty);
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).to(stringJavaFXProperty, stringBooleanConverter);
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).to(readOnlyStringProperty, stringBooleanConverter);
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).to(stringJavaFXProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).to(readOnlyStringProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();

        binding = FXBinder.bind(booleanDolphinProperty).bidirectionalTo(stringJavaFXProperty, booleanStringBidirectionalConverter.invert());
        binding.unbind();
        binding = FXBinder.bind(booleanDolphinProperty).bidirectionalTo(booleanJavaFXProperty);
        binding.unbind();

        binding = FXBinder.bind(stringJavaFXProperty).to(stringDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(stringJavaFXProperty).to(booleanDolphinProperty, booleanStringConverter);
        binding.unbind();
        binding = FXBinder.bind(stringJavaFXProperty).to(booleanDolphinProperty, booleanStringBidirectionalConverter);
        binding.unbind();
        binding = FXBinder.bind(writableStringValue).to(stringDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(writableStringValue).to(booleanDolphinProperty, booleanStringConverter);
        binding.unbind();
        binding = FXBinder.bind(writableStringValue).to(booleanDolphinProperty, booleanStringBidirectionalConverter);
        binding.unbind();

        binding = FXBinder.bind(stringJavaFXProperty).bidirectionalTo(stringDolphinProperty);
        binding.unbind();
        binding = FXBinder.bind(stringJavaFXProperty).bidirectionalTo(booleanDolphinProperty, booleanStringBidirectionalConverter);
        binding.unbind();

        binding = FXBinder.bind(stringDolphinProperty).to(stringJavaFXProperty);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).to(readOnlyStringProperty);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).to(booleanJavaFXProperty, booleanStringConverter);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).to(readOnlyBooleanProperty, booleanStringConverter);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).to(booleanJavaFXProperty, booleanStringBidirectionalConverter);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).to(readOnlyBooleanProperty, booleanStringBidirectionalConverter);
        binding.unbind();

        binding = FXBinder.bind(stringDolphinProperty).bidirectionalTo(booleanJavaFXProperty, booleanStringBidirectionalConverter);
        binding.unbind();
        binding = FXBinder.bind(stringDolphinProperty).bidirectionalTo(stringJavaFXProperty);
        binding.unbind();
    }

}
