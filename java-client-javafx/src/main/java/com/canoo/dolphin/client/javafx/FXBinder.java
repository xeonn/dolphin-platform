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

    public static JavaFXBinder<Double> bind(WritableDoubleValue writableDoubleValue) {
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Float> bind(WritableFloatValue writableDoubleValue) {
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Integer> bind(WritableIntegerValue writableDoubleValue) {
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static JavaFXBinder<Long> bind(WritableLongValue writableDoubleValue) {
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static NumericJavaFXBidirectionaBinder<Double> bind(DoubleProperty property) {
        return new DoubleJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Float> bind(FloatProperty property) {
        return new FloatJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Integer> bind(IntegerProperty property) {
        return new IntegerJavaFXBidirectionalBinder(property);
    }

    public static NumericJavaFXBidirectionaBinder<Long> bind(LongProperty property) {
        return new LongJavaFXBidirectionalBinder(property);
    }

    public static <T> JavaFXBinder<T> bind(WritableValue<T> writableDoubleValue) {
        return new DefaultJavaFXBinder(writableDoubleValue);
    }

    public static <T> JavaFXBidirectionalBinder<T> bind(javafx.beans.property.Property<T> property) {
        return new DefaultJavaFXBidirectionalBinder<>(property);
    }

    public static <T> DolphinBinder<T> bind(Property<T> property) {
        return new DefaultDolphinBinder<>(property);
    }

    public static NumericDolphinBinder<Double> bindDouble(Property<Double> property) {
        return new DoubleDolphinBinder(property);
    }

    public static NumericDolphinBinder<Float> bindFloat(Property<Float> property) {
        return new FloatDolphinBinder(property);
    }

    public static NumericDolphinBinder<Integer> bindInteger(Property<Integer> property) {
        return new IntegerDolphinBinder(property);
    }

    public static NumericDolphinBinder<Long> bindLong(Property<Long> property) {
        return new LongDolphinBinder(property);
    }
}
