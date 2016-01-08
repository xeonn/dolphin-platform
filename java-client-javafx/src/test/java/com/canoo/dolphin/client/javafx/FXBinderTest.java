package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.collections.ObservableArrayList;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.*;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableStringValue;
import javafx.collections.FXCollections;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FXBinderTest {

    private final static double EPSILON = 1e-10;

    @Test
    public void testJavaFXDoubleUnidirectional() {
        Property<Double> doubleDolphinProperty = new MockedProperty<>();
        Property<Number> numberDolphinProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();

        doubleDolphinProperty.set(47.0);
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(doubleDolphinProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        doubleDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberDolphinProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).to(numberDolphinProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        numberDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleDolphinProperty.set(47.0);
        binding = FXBinder.bind(writableDoubleValue).to(doubleDolphinProperty);
        assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        doubleDolphinProperty.set(100.0);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectional() {
        Property<Double> doubleDolphinProperty = new MockedProperty<>();
        Property<Number> numberDolphinProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();

        doubleDolphinProperty.set(47.0);
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(doubleDolphinProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        doubleDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        doubleDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(doubleDolphinProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        assertEquals(doubleDolphinProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        doubleDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        numberDolphinProperty.set(12.0);
        binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalTo(numberDolphinProperty);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        numberDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(numberDolphinProperty.get().doubleValue(), 12.0, EPSILON);
        doubleJavaFXProperty.setValue(null);
        assertEquals(numberDolphinProperty.get().doubleValue(), 0.0, EPSILON);

        binding.unbind();
        numberDolphinProperty.set(100.0);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleUnidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        WritableDoubleValue writableDoubleValue = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);

        stringDolphinProperty.set("47.0");
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringDolphinProperty.set("12.0");
        binding = FXBinder.bind(doubleJavaFXProperty).to(stringDolphinProperty, stringDoubleConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 12.0, EPSILON);
        stringDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);


        stringDolphinProperty.set("47.0");
        binding = FXBinder.bind(writableDoubleValue).to(stringDolphinProperty, stringDoubleConverter);
        assertEquals(writableDoubleValue.get(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        assertEquals(writableDoubleValue.get(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
        binding.unbind();
        stringDolphinProperty.set("100.0");
        assertEquals(writableDoubleValue.get(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXDoubleBidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        DoubleProperty doubleJavaFXProperty = new SimpleDoubleProperty();
        Converter<String, Double> stringDoubleConverter = s -> s == null ? null : Double.parseDouble(s);
        Converter<Double, String> doubleStringConverter = d -> d == null ? null : d.toString();
        BidirectionalConverter<String, Double> doubleBidirectionalConverter = new DefaultBidirectionalConverter<>(stringDoubleConverter, doubleStringConverter);

        stringDolphinProperty.set("47.0");
        assertNotEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);

        Binding binding = FXBinder.bind(doubleJavaFXProperty).bidirectionalToNumeric(stringDolphinProperty, doubleBidirectionalConverter);
        assertEquals(doubleJavaFXProperty.doubleValue(), 47.0, EPSILON);
        stringDolphinProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 100.0, EPSILON);
        stringDolphinProperty.set(null);
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);

        doubleJavaFXProperty.set(12.0);
        assertEquals(stringDolphinProperty.get(), "12.0");
        doubleJavaFXProperty.setValue(null);
        assertEquals(stringDolphinProperty.get(), "0.0");

        binding.unbind();
        stringDolphinProperty.set("100.0");
        assertEquals(doubleJavaFXProperty.doubleValue(), 0.0, EPSILON);
    }

    @Test
    public void testJavaFXBooleanUnidirectional() {
        Property<Boolean> booleanDolphinProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();

        booleanDolphinProperty.set(true);
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(booleanDolphinProperty);
        assertEquals(booleanJavaFXProperty.get(), true);
        booleanDolphinProperty.set(false);
        assertEquals(booleanJavaFXProperty.get(), false);
        booleanDolphinProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        booleanDolphinProperty.set(true);
        assertEquals(booleanJavaFXProperty.get(), false);


        binding = FXBinder.bind(writableBooleanValue).to(booleanDolphinProperty);
        assertEquals(writableBooleanValue.get(), true);
        booleanDolphinProperty.set(false);
        assertEquals(writableBooleanValue.get(), false);
        booleanDolphinProperty.set(null);
        assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        booleanDolphinProperty.set(true);
        assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanUnidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        WritableBooleanValue writableBooleanValue = new SimpleBooleanProperty();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);

        stringDolphinProperty.set("Hello");
        assertEquals(booleanJavaFXProperty.get(), false);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).to(stringDolphinProperty, stringBooleanConverter);
        assertEquals(booleanJavaFXProperty.get(), false);
        stringDolphinProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), true);
        stringDolphinProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);
        binding.unbind();
        stringDolphinProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), false);

        stringDolphinProperty.set("false");
        binding = FXBinder.bind(writableBooleanValue).to(stringDolphinProperty, stringBooleanConverter);
        assertEquals(writableBooleanValue.get(), false);
        stringDolphinProperty.set("true");
        assertEquals(writableBooleanValue.get(), true);
        stringDolphinProperty.set(null);
        assertEquals(writableBooleanValue.get(), false);
        binding.unbind();
        stringDolphinProperty.set("true");
        assertEquals(writableBooleanValue.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectional() {
        Property<Boolean> booleanDolphinProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();

        booleanDolphinProperty.set(true);
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(booleanDolphinProperty);
        assertEquals(booleanJavaFXProperty.get(), true);
        booleanDolphinProperty.set(false);
        assertEquals(booleanJavaFXProperty.get(), false);
        booleanDolphinProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        assertEquals(booleanDolphinProperty.get().booleanValue(), true);

        booleanJavaFXProperty.setValue(null);
        assertEquals(booleanDolphinProperty.get().booleanValue(), false);

        binding.unbind();
        booleanDolphinProperty.set(true);
        assertEquals(booleanJavaFXProperty.get(), false);
    }

    @Test
    public void testJavaFXBooleanBidirectionalWithConverter() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        BooleanProperty booleanJavaFXProperty = new SimpleBooleanProperty();
        Converter<Boolean, String> booleanStringConverter = b -> b == null ? null : b.toString();
        Converter<String, Boolean> stringBooleanConverter = s -> s == null ? null : Boolean.parseBoolean(s);
        BidirectionalConverter<Boolean, String> booleanStringBidirectionalConverter = new DefaultBidirectionalConverter<>(booleanStringConverter, stringBooleanConverter);


        stringDolphinProperty.set("true");
        assertNotEquals(booleanJavaFXProperty.get(), true);

        Binding binding = FXBinder.bind(booleanJavaFXProperty).bidirectionalTo(stringDolphinProperty, booleanStringBidirectionalConverter.invert());
        assertEquals(booleanJavaFXProperty.get(), true);
        stringDolphinProperty.set("false");
        assertEquals(booleanJavaFXProperty.get(), false);
        stringDolphinProperty.set(null);
        assertEquals(booleanJavaFXProperty.get(), false);


        booleanJavaFXProperty.set(true);
        assertEquals(stringDolphinProperty.get(), "true");

        booleanJavaFXProperty.setValue(null);
        assertEquals(stringDolphinProperty.get(), "false");

        binding.unbind();
        stringDolphinProperty.set("true");
        assertEquals(booleanJavaFXProperty.get(), false);
    }

    @Test
    public void testJavaFXStringUnidirectional() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();
        WritableStringValue writableStringValue = new SimpleStringProperty();

        stringDolphinProperty.set("Hello");
        assertNotEquals(stringJavaFXProperty.get(), "Hello");

        Binding binding = FXBinder.bind(stringJavaFXProperty).to(stringDolphinProperty);
        assertEquals(stringJavaFXProperty.get(), "Hello");
        stringDolphinProperty.set("Hello JavaFX");
        assertEquals(stringJavaFXProperty.get(), "Hello JavaFX");
        stringDolphinProperty.set(null);
        assertEquals(stringJavaFXProperty.get(), null);
        binding.unbind();
        stringDolphinProperty.set("Hello JavaFX");
        assertEquals(stringJavaFXProperty.get(), null);


        binding = FXBinder.bind(writableStringValue).to(stringDolphinProperty);
        assertEquals(writableStringValue.get(), "Hello JavaFX");
        stringDolphinProperty.set("Dolphin Platform");
        assertEquals(writableStringValue.get(), "Dolphin Platform");
        stringDolphinProperty.set(null);
        assertEquals(writableStringValue.get(), null);
        binding.unbind();
        stringDolphinProperty.set("Dolphin Platform");
        assertEquals(writableStringValue.get(), null);
    }

    @Test
    public void testJavaFXStringBidirectional() {
        Property<String> stringDolphinProperty = new MockedProperty<>();
        StringProperty stringJavaFXProperty = new SimpleStringProperty();

        stringDolphinProperty.set("Hello");
        assertNotEquals(stringJavaFXProperty.get(), "Hello");

        Binding binding = FXBinder.bind(stringJavaFXProperty).bidirectionalTo(stringDolphinProperty);
        assertEquals(stringJavaFXProperty.get(), "Hello");
        stringDolphinProperty.set("Hello World");
        assertEquals(stringJavaFXProperty.get(), "Hello World");
        stringDolphinProperty.set(null);
        assertEquals(stringJavaFXProperty.get(), null);


        stringJavaFXProperty.set("Hello from JavaFX");
        assertEquals(stringDolphinProperty.get(), "Hello from JavaFX");

        stringJavaFXProperty.setValue(null);
        assertEquals(stringDolphinProperty.get(), null);

        binding.unbind();
        stringDolphinProperty.set("Hello Dolphin");
        assertEquals(stringJavaFXProperty.get(), null);
    }


    @Test
    public void testJavaFXIntegerUnidirectional() {
        Property<Integer> integerDolphinProperty = new MockedProperty<>();
        Property<Number> numberDolphinProperty = new MockedProperty<>();
        IntegerProperty integerJavaFXProperty = new SimpleIntegerProperty();
        WritableIntegerValue writableIntegerValue = new SimpleIntegerProperty();

        integerDolphinProperty.set(47);
        assertNotEquals(integerJavaFXProperty.doubleValue(), 47);

        Binding binding = FXBinder.bind(integerJavaFXProperty).to(integerDolphinProperty);
        assertEquals(integerJavaFXProperty.get(), 47);
        integerDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 100);
        integerDolphinProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);
        binding.unbind();
        integerDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);


        numberDolphinProperty.set(12);
        binding = FXBinder.bind(integerJavaFXProperty).to(numberDolphinProperty);
        assertEquals(integerJavaFXProperty.get(), 12);
        numberDolphinProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);
        binding.unbind();
        numberDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerDolphinProperty.set(47);
        binding = FXBinder.bind(writableIntegerValue).to(integerDolphinProperty);
        assertEquals(writableIntegerValue.get(), 47);
        integerDolphinProperty.set(100);
        assertEquals(writableIntegerValue.get(), 100);
        integerDolphinProperty.set(null);
        assertEquals(writableIntegerValue.get(), 0);
        binding.unbind();
        integerDolphinProperty.set(100);
        assertEquals(writableIntegerValue.get(), 0);
    }

    @Test
    public void testJavaFXIntegerBidirectional() {
        Property<Integer> integerDolphinProperty = new MockedProperty<>();
        Property<Number> numberDolphinProperty = new MockedProperty<>();
        IntegerProperty integerJavaFXProperty = new SimpleIntegerProperty();

        integerDolphinProperty.set(47);
        assertNotEquals(integerJavaFXProperty.get(), 47);

        Binding binding = FXBinder.bind(integerJavaFXProperty).bidirectionalToNumeric(integerDolphinProperty);
        assertEquals(integerJavaFXProperty.get(), 47);
        integerDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 100);
        integerDolphinProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerJavaFXProperty.set(12);
        assertEquals(integerDolphinProperty.get().intValue(), 12);
        integerJavaFXProperty.setValue(null);
        assertEquals(integerDolphinProperty.get().intValue(), 0);

        binding.unbind();
        integerDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);


        numberDolphinProperty.set(12);
        binding = FXBinder.bind(integerJavaFXProperty).bidirectionalTo(numberDolphinProperty);
        assertEquals(integerJavaFXProperty.get(), 12);
        numberDolphinProperty.set(null);
        assertEquals(integerJavaFXProperty.get(), 0);

        integerJavaFXProperty.set(12);
        assertEquals(numberDolphinProperty.get().intValue(), 12);
        integerJavaFXProperty.setValue(null);
        assertEquals(numberDolphinProperty.get().intValue(), 0);

        binding.unbind();
        numberDolphinProperty.set(100);
        assertEquals(integerJavaFXProperty.get(), 0);
    }

    @Test
    public void testUnidirectionalChain() {
        Property<String> stringDolphinProperty1 = new MockedProperty<>();
        StringProperty stringJavaFXProperty1 = new SimpleStringProperty();
        Property<String> stringDolphinProperty2 = new MockedProperty<>();
        StringProperty stringJavaFXProperty2 = new SimpleStringProperty();

        Binding binding1 = FXBinder.bind(stringDolphinProperty1).to(stringJavaFXProperty1);
        Binding binding2 = FXBinder.bind(stringJavaFXProperty2).to(stringDolphinProperty1);
        Binding binding3 = FXBinder.bind(stringDolphinProperty2).to(stringJavaFXProperty2);

        stringJavaFXProperty1.setValue("Hello");

        assertEquals(stringDolphinProperty1.get(), "Hello");
        assertEquals(stringDolphinProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        binding2.unbind();

        stringJavaFXProperty1.setValue("Hello World");

        assertEquals(stringDolphinProperty1.get(), "Hello World");
        assertEquals(stringDolphinProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        binding1.unbind();
        binding3.unbind();
    }

    @Test
    public void testBidirectionalChain() {
        Property<String> stringDolphinProperty1 = new MockedProperty<>();
        StringProperty stringJavaFXProperty1 = new SimpleStringProperty();
        Property<String> stringDolphinProperty2 = new MockedProperty<>();
        StringProperty stringJavaFXProperty2 = new SimpleStringProperty();

        Binding binding1 = FXBinder.bind(stringDolphinProperty1).bidirectionalTo(stringJavaFXProperty1);
        Binding binding2 = FXBinder.bind(stringJavaFXProperty2).bidirectionalTo(stringDolphinProperty1);
        Binding binding3 = FXBinder.bind(stringDolphinProperty2).bidirectionalTo(stringJavaFXProperty2);

        stringJavaFXProperty1.setValue("Hello");
        assertEquals(stringDolphinProperty1.get(), "Hello");
        assertEquals(stringDolphinProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        stringDolphinProperty1.set("Hello World");
        assertEquals(stringDolphinProperty1.get(), "Hello World");
        assertEquals(stringDolphinProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        stringJavaFXProperty2.setValue("Hello");
        assertEquals(stringDolphinProperty1.get(), "Hello");
        assertEquals(stringDolphinProperty2.get(), "Hello");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello");

        stringDolphinProperty2.set("Hello World");
        assertEquals(stringDolphinProperty1.get(), "Hello World");
        assertEquals(stringDolphinProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello World");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        binding2.unbind();

        stringJavaFXProperty1.setValue("Hello");
        assertEquals(stringDolphinProperty1.get(), "Hello");
        assertEquals(stringDolphinProperty2.get(), "Hello World");
        assertEquals(stringJavaFXProperty1.get(), "Hello");
        assertEquals(stringJavaFXProperty2.get(), "Hello World");

        binding1.unbind();
        binding3.unbind();
    }

    @Test
    public void testListBinding() {
        ObservableList<String> dolphinList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(dolphinList);

        assertEquals(dolphinList.size(), 0);
        assertEquals(javaFXList.size(), 0);

        dolphinList.add("Hello");

        assertEquals(dolphinList.size(), 1);
        assertEquals(javaFXList.size(), 1);

        dolphinList.add("World");
        dolphinList.add("Dolphin");

        assertEquals(dolphinList.size(), 3);
        assertEquals(javaFXList.size(), 3);

        dolphinList.clear();

        assertEquals(dolphinList.size(), 0);
        assertEquals(javaFXList.size(), 0);

        dolphinList.add("Java");

        assertEquals(dolphinList.size(), 1);
        assertEquals(javaFXList.size(), 1);

        binding.unbind();

        assertEquals(dolphinList.size(), 1);
        assertEquals(javaFXList.size(), 1);

        dolphinList.add("Duke");

        assertEquals(dolphinList.size(), 2);
        assertEquals(javaFXList.size(), 1);
    }

    @Test
    public void severalBindings() {
        ObservableList<String> dolphinList1 = new ObservableArrayList<>();
        ObservableList<String> dolphinList2 = new ObservableArrayList<>();
        ObservableList<String> dolphinList3 = new ObservableArrayList<>();
        ObservableList<String> dolphinList4 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList1 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList2 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList3 = FXCollections.observableArrayList();
        javafx.collections.ObservableList<String> javaFXList4 = FXCollections.observableArrayList();

        Binding binding1 = FXBinder.bind(javaFXList1).to(dolphinList1);
        Binding binding2 = FXBinder.bind(javaFXList2).to(dolphinList2);
        Binding binding3 = FXBinder.bind(javaFXList3).to(dolphinList3);
        Binding binding4 = FXBinder.bind(javaFXList4).to(dolphinList4);

        binding1.unbind();
        binding2.unbind();

        binding1 = FXBinder.bind(javaFXList1).to(dolphinList2);
        binding2 = FXBinder.bind(javaFXList2).to(dolphinList1);

        binding3.unbind();
        binding4.unbind();

        binding3 = FXBinder.bind(javaFXList3).to(dolphinList4);
        binding4 = FXBinder.bind(javaFXList4).to(dolphinList3);

        binding1.unbind();
        binding2.unbind();
        binding3.unbind();
        binding4.unbind();

        binding1 = FXBinder.bind(javaFXList1).to(dolphinList4);
        binding2 = FXBinder.bind(javaFXList2).to(dolphinList3);
        binding3 = FXBinder.bind(javaFXList3).to(dolphinList2);
        binding4 = FXBinder.bind(javaFXList4).to(dolphinList1);

        binding1.unbind();
        binding2.unbind();
        binding3.unbind();
        binding4.unbind();
    }


    @Test
    public void testErrorOnMultipleListBinding() {
        ObservableList<String> dolphinList = new ObservableArrayList<>();
        ObservableList<String> dolphinList2 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        FXBinder.bind(javaFXList).to(dolphinList);
        try {
            FXBinder.bind(javaFXList).to(dolphinList2);
            fail("A JavaFX list can only be bound to one Dolphin list");
        } catch (Exception e){
        }
    }

    @Test
    public void testCorrectUnbind() {
        ObservableList<String> dolphinList = new ObservableArrayList<>();
        ObservableList<String> dolphinList2 = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(dolphinList);

        dolphinList.add("Foo");

        assertEquals(dolphinList.size(), 1);
        assertEquals(dolphinList2.size(), 0);
        assertEquals(javaFXList.size(), 1);

        binding.unbind();
        FXBinder.bind(javaFXList).to(dolphinList2);

        assertEquals(dolphinList.size(), 1);
        assertEquals(dolphinList2.size(), 0);
        assertEquals(javaFXList.size(), 0);

        dolphinList2.add("Foo");
        dolphinList2.add("Bar");

        assertEquals(dolphinList.size(), 1);
        assertEquals(dolphinList2.size(), 2);
        assertEquals(javaFXList.size(), 2);
    }

    @Test
    public void testConvertedListBinding() {
        ObservableList<Boolean> dolphinList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        Binding binding = FXBinder.bind(javaFXList).to(dolphinList, value -> value.toString());

        dolphinList.add(true);

        assertEquals(dolphinList.size(), 1);
        assertEquals(javaFXList.size(), 1);

        assertEquals(javaFXList.get(0), "true");

    }

    @Test
    public void testSeveralBinds() {
        ObservableList<String> dolphinList = new ObservableArrayList<>();
        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        for(int i = 0; i < 10; i++) {
            Binding binding = FXBinder.bind(javaFXList).to(dolphinList);
            binding.unbind();
        }

        dolphinList.addAll(Arrays.asList("A", "B", "C"));
        for(int i = 0; i < 10; i++) {
            Binding binding = FXBinder.bind(javaFXList).to(dolphinList);
            binding.unbind();
        }
    }

    @Test
    public void testListBindingWithNonEmptyLists() {
        ObservableList<String> dolphinList = new ObservableArrayList<>();

        javafx.collections.ObservableList<String> javaFXList = FXCollections.observableArrayList();

        dolphinList.addAll(Arrays.asList("A", "B", "C"));
        Binding binding1 = FXBinder.bind(javaFXList).to(dolphinList);
        assertEquals(dolphinList.size(), 3);
        assertEquals(javaFXList.size(), 3);
        assertTrue(dolphinList.contains("A"));
        assertTrue(dolphinList.contains("B"));
        assertTrue(dolphinList.contains("C"));
        assertTrue(javaFXList.contains("A"));
        assertTrue(javaFXList.contains("B"));
        assertTrue(javaFXList.contains("C"));

        binding1.unbind();

        dolphinList.clear();
        javaFXList.clear();
        javaFXList.addAll("A", "B", "C");
        Binding binding2 = FXBinder.bind(javaFXList).to(dolphinList);
        assertEquals(dolphinList.size(), 0);
        assertEquals(javaFXList.size(), 0);


        binding2.unbind();

        dolphinList.clear();
        javaFXList.clear();
        dolphinList.addAll(Arrays.asList("A", "B", "C"));
        javaFXList.addAll("D", "E", "F");
        FXBinder.bind(javaFXList).to(dolphinList);
        assertEquals(dolphinList.size(), 3);
        assertEquals(javaFXList.size(), 3);
        assertTrue(dolphinList.contains("A"));
        assertTrue(dolphinList.contains("B"));
        assertTrue(dolphinList.contains("C"));
        assertTrue(javaFXList.contains("A"));
        assertTrue(javaFXList.contains("B"));
        assertTrue(javaFXList.contains("C"));
    }
}
