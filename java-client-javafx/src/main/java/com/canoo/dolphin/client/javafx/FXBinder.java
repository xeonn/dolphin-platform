/*
 * Copyright 2015 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.impl.DefaultDolphinBinder;
import com.canoo.dolphin.client.javafx.impl.DefaultJavaFXBidirectionalBinder;
import com.canoo.dolphin.client.javafx.impl.DefaultJavaFXBinder;
import com.canoo.dolphin.client.javafx.impl.DefaultJavaFXListBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.DoubleDolphinBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.DoubleJavaFXBidirectionalBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.FloatDolphinBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.FloatJavaFXBidirectionalBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.IntegerDolphinBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.IntegerJavaFXBidirectionalBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.LongDolphinBinder;
import com.canoo.dolphin.client.javafx.impl.numeric.LongJavaFXBidirectionalBinder;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;

import static com.canoo.dolphin.util.Assert.requireNonNull;

/**
 * Method to create JavaFX property wrappers for dolphin platform properties
 * This will be changed in the next version to a fluent API like FXBinder.bind(jfxp).to(dp);
 */
public final class FXBinder {

    private FXBinder() {
    }

    public static <T> JavaFXListBinder<T> bind(ObservableList<T> list) {
        requireNonNull(list, "list");
        return new DefaultJavaFXListBinder(list);
    }

    public static JavaFXBinder<Double> bind(WritableDoubleValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Float> bind(WritableFloatValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Integer> bind(WritableIntegerValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Long> bind(WritableLongValue writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static NumericJavaFXBidirectionaBinder<Double> bind(DoubleProperty property) {
        requireNonNull(property, "property");
        return new DoubleJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Float> bind(FloatProperty property) {
        requireNonNull(property, "property");
        return new FloatJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Integer> bind(IntegerProperty property) {
        requireNonNull(property, "property");
        return new IntegerJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Long> bind(LongProperty property) {
        requireNonNull(property, "property");
        return new LongJavaFXBidirectionalBinder(property);
    }

    public static <T> JavaFXBinder<T> bind(WritableValue<T> writableDoubleValue) {
        requireNonNull(writableDoubleValue, "writableDoubleValue");
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static <T> JavaFXBidirectionalBinder<T> bind(javafx.beans.property.Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultJavaFXBidirectionalBinder<>(property);
    }

    public static <T> DolphinBinder<T> bind(Property<T> property) {
        requireNonNull(property, "property");
        return new DefaultDolphinBinder<>(property);
    }

    public static NumericDolphinBinder<Double> bindDouble(Property<Double> property) {
        requireNonNull(property, "property");
        return new DoubleDolphinBinder(property);
    }

    public static NumericDolphinBinder<Float> bindFloat(Property<Float> property) {
        requireNonNull(property, "property");
        return new FloatDolphinBinder(property);
    }

    public static NumericDolphinBinder<Integer> bindInteger(Property<Integer> property) {
        requireNonNull(property, "property");
        return new IntegerDolphinBinder(property);
    }

    public static NumericDolphinBinder<Long> bindLong(Property<Long> property) {
        requireNonNull(property, "property");
        return new LongDolphinBinder(property);
    }


}
