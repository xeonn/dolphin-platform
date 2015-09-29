package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.impl.PropertyImpl;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FXBinderTest {

    private final static double EPSILON = 1e-10;

    @Test
    public void testJavaFXDoubleUnidirectional() {
        Property<Double> doubleDolphinProperty = new PropertyImpl<>();
        Property<Number> numberDolphinProperty = new PropertyImpl<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();

        doubleDolphinProperty.set(47.0);
        Assert.assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(doubleDolphinProperty);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberDolphinProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).to(numberDolphinProperty);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        numberDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleDolphinProperty.set(47.0);
        binding = FXBinder.bind(writableDoubleValue).to(doubleDolphinProperty);
        Assert.assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        Assert.assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectional() {
        Property<Double> doubleDolphinProperty = new PropertyImpl<>();
        Property<Number> numberDolphinProperty = new PropertyImpl<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();

        doubleDolphinProperty.set(47.0);
        Assert.assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(doubleDolphinProperty);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        Assert.assertEquals(doubleDolphinProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        doubleDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberDolphinProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalTo(numberDolphinProperty);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        Assert.assertEquals(numberDolphinProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        Assert.assertEquals(numberDolphinProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        numberDolphinProperty.set(100.0);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleUnidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new PropertyImpl<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);

        stringDolphinProperty.set("47.0");
        Assert.assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringDolphinProperty.set("12.0");
        binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        stringDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringDolphinProperty.set("47.0");
        binding = FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, stringDoubleConverter);
        Assert.assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        Assert.assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new PropertyImpl<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);
        Converter<Double, String> doubleStringConverter = d -> d == null ? null : d.toString();
        BidirectionalConverter<String, Double> doubleBidirectionalConverter = new DefaultBidirectionalConverter<>(stringDoubleConverter, doubleStringConverter);

        stringDolphinProperty.set("47.0");
        Assert.assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(stringDolphinProperty, doubleBidirectionalConverter);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        Assert.assertEquals(stringDolphinProperty.get(), "12.0");
        doubleJavaFXProperty.setValue(null);
        Assert.assertEquals(stringDolphinProperty.get(), "0.0");

        binding.unbind();
        stringDolphinProperty.set("100.0");
        Assert.assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXBooleanUnidirectional() {
        Property<Boolean> booleanDolphinProperty = new PropertyImpl<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();

        booleanDolphinProperty.set(true);
        Assert.assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(booleanDolphinProperty);
        Assert.assertEquals(booleanJavaFXProperty.get(), true);
        booleanDolphinProperty.set(false);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        booleanDolphinProperty.set(null);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        booleanDolphinProperty.set(true);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);


        binding = FXBinder.bind(writableBooleanValue).to(booleanDolphinProperty);
        Assert.assertEquals(writableBooleanValue.get(), true);
        booleanDolphinProperty.set(false);
        Assert.assertEquals(writableBooleanValue.get(), false);
        booleanDolphinProperty.set(null);
        Assert.assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        booleanDolphinProperty.set(true);
        Assert.assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanUnidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new PropertyImpl<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);


        stringDolphinProperty.set("Hello");
        Assert.assertEquals(booleanJavaFXProperty.get(), false);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, stringBooleanConverter);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        stringDolphinProperty.set("true");
        Assert.assertEquals(booleanJavaFXProperty.get(), true);
        stringDolphinProperty.set(null);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        stringDolphinProperty.set("true");
        Assert.assertEquals(booleanJavaFXProperty.get(), false);

        stringDolphinProperty.set("false");
        binding = FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, stringBooleanConverter);
        Assert.assertEquals(writableBooleanValue.get(), false);
        stringDolphinProperty.set("true");
        Assert.assertEquals(writableBooleanValue.get(), true);
        stringDolphinProperty.set(null);
        Assert.assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        stringDolphinProperty.set("true");
        Assert.assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectional() {
        Property<Boolean> booleanDolphinProperty = new PropertyImpl<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();

        booleanDolphinProperty.set(true);
        Assert.assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(booleanDolphinProperty);
        Assert.assertEquals(booleanJavaFXProperty.get(), true);
        booleanDolphinProperty.set(false);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        booleanDolphinProperty.set(null);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        Assert.assertEquals(booleanDolphinProperty.get().booleanValue(), true);

        booleanJavaFXProperty.setValue(null);
        Assert.assertEquals(booleanDolphinProperty.get().booleanValue(), false);

        binding.unbind();
        booleanDolphinProperty.set(true);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new PropertyImpl<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        Converter<Boolean, String> booleanStringConverter = b -> b == null ? null : b.toString();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);
        BidirectionalConverter<Boolean, String> booleanStringBidirectionalConverter = new DefaultBidirectionalConverter<>(booleanStringConverter, stringBooleanConverter);


        stringDolphinProperty.set("true");
        Assert.assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        Assert.assertEquals(booleanJavaFXProperty.get(), true);
        stringDolphinProperty.set("false");
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
        stringDolphinProperty.set(null);
        Assert.assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        Assert.assertEquals(stringDolphinProperty.get(), "true");

        booleanJavaFXProperty.setValue(null);
        Assert.assertEquals(stringDolphinProperty.get(), "false");

        binding.unbind();
        stringDolphinProperty.set("true");
        Assert.assertEquals(booleanJavaFXProperty.get(), false);
    }

}
