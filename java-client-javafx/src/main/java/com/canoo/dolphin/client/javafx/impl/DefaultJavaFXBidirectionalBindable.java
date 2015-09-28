package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.client.javafx.JavaFXBidirectionalBindable;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.value.ChangeListener;

/**
 * Created by hendrikebbers on 28.09.15.
 */
public class DefaultJavaFXBidirectionalBindable<S> extends DefaultJavaFXBindable<S> implements JavaFXBidirectionalBindable<S> {

    private final javafx.beans.property.Property<S> javaFxProperty;

    public DefaultJavaFXBidirectionalBindable(final javafx.beans.property.Property<S> javaFxProperty) {
        super(javaFxProperty);
        this.javaFxProperty = javaFxProperty;
    }

    @Override
    public <T> Subscription bidirectionalTo(final Property<T> property, BidirectionalConverter<T, S> converter) {
        final Subscription unidirectionalSubscription = to(property, converter);

        final ChangeListener<S> listener = (obs, oldVal, newVal) -> property.set(converter.convertBack(newVal));
        javaFxProperty.addListener(listener);
        return () -> {
            javaFxProperty.removeListener(listener);
            unidirectionalSubscription.unsubscribe();
        };
    }

}

