package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.client.javafx.Binding;
import com.canoo.dolphin.client.javafx.JavaFXBidirectionalBindable;
import com.canoo.dolphin.client.javafx.NumericJavaFXBidirectionaBindable;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;


/**
 * Created by hendrikebbers on 27.09.15.
 */
public class DoubleJavaFXBidirectionalBindable extends DefaultJavaFXBindable<Number> implements NumericJavaFXBidirectionaBindable<Double> {

    private final DoubleProperty javaFxProperty;

    public DoubleJavaFXBidirectionalBindable(final DoubleProperty javaFxProperty) {
        super(javaFxProperty);
        this.javaFxProperty = javaFxProperty;
    }

    @Override
    public <T> Binding bidirectionalTo(final Property<T> property, BidirectionalConverter<T, Number> converter) {
        final Binding unidirectionalBinding = to(property, converter);

        final ChangeListener<Number> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(newVal));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalBinding.unbind();
        };
    }

    @Override
    public Double convertNumber(Number value) {
        if(value == null) {
            return null;
        }
        return value.doubleValue();
    }

    @Override
    public <T> Binding bidirectionalToNumeric(Property<T> property, BidirectionalConverter<T, Double> converter) {
        final Binding unidirectionalBinding = to(property, converter);

        final ChangeListener<Number> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(new Double(newVal.doubleValue())));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalBinding.unbind();
        };
    }
}
