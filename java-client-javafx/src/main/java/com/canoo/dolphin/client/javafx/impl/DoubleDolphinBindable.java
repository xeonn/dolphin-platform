package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.client.javafx.DolphinBindable;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public class DoubleDolphinBindable implements DolphinBindable<Number> {

    private final Property<Double> property;

    public DoubleDolphinBindable(final Property<Double> property) {
        if(property == null) {
            throw new IllegalArgumentException("property must not be null");
        }
        this.property = property;
    }

    @Override
    public <T> Subscription to(final ObservableValue<T> observableValue, final Converter<T, Number> converter) {
        if(observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        if(converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        final ChangeListener<T> listener = (obs, oldVal, newVal) -> property.set(converter.convert(newVal).doubleValue());
        observableValue.addListener(listener);
        property.set(converter.convert(observableValue.getValue()).doubleValue());
        return () -> observableValue.removeListener(listener);
    }

    @Override
    public Subscription bidirectionalTo(final javafx.beans.property.Property<Number> javaFxProperty) {
        if(javaFxProperty == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        return bidirectionalTo(javaFxProperty, new BidirectionalConverter<Number, Number>() {
            @Override
            public Number convertBack(Number value) {
                return value;
            }

            @Override
            public Number convert(Number value) {
                return value;
            }
        });
    }

    @Override
    public <T> Subscription bidirectionalTo(javafx.beans.property.Property<T> javaFxProperty, BidirectionalConverter<T, Number> converter) {
        if(javaFxProperty == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        if(converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        Subscription toSubscription = to(javaFxProperty, converter);
        Subscription subscription = property.onChanged(e -> javaFxProperty.setValue(converter.convertBack(property.get())));
        return () -> {
            toSubscription.unsubscribe();
            subscription.unsubscribe();
        };
    }
}
