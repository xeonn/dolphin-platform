package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.client.javafx.DolphinBindable;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 28.09.15.
 */
public class DefaultDolphinBindable<S> implements DolphinBindable<S> {

    private final Property<S> property;

    public DefaultDolphinBindable(final Property<S> property) {
        if (property == null) {
            throw new IllegalArgumentException("property must not be null");
        }
        this.property = property;
    }

    @Override
    public <T> Subscription to(final ObservableValue<T> observableValue, final Converter<? super T, ? extends S> converter) {
        if (observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        final ChangeListener<T> listener = (obs, oldVal, newVal) -> property.set(converter.convert(newVal));
        observableValue.addListener(listener);
        property.set(converter.convert(observableValue.getValue()));
        return () -> observableValue.removeListener(listener);
    }


    @Override
    public <T> Subscription bidirectionalTo(javafx.beans.property.Property<T> javaFxProperty, BidirectionalConverter<T, S> converter) {
        if (javaFxProperty == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        if (converter == null) {
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
