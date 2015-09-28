package com.canoo.dolphin.client.javafx.impl;

import com.canoo.dolphin.client.javafx.Converter;
import com.canoo.dolphin.client.javafx.JavaFXBindable;
import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.value.WritableValue;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public class DefaultJavaFXBindable<S> implements JavaFXBindable<S> {

    private final WritableValue<S> javaFxValue;

    public DefaultJavaFXBindable(final WritableValue<S> javaFxValue) {
        if (javaFxValue == null) {
            throw new IllegalArgumentException("javaFxValue must not be null");
        }
        this.javaFxValue = javaFxValue;
    }

    @Override
    public <T> Subscription to(Property<T> dolphinProperty, Converter<T, ? extends S> converter) {
        if (dolphinProperty == null) {
            throw new IllegalArgumentException("dolphinProperty must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("converter must not be null");
        }
        final Subscription subscription = dolphinProperty.onChanged(event -> javaFxValue.setValue(converter.convert(dolphinProperty.get())));
        javaFxValue.setValue(converter.convert(dolphinProperty.get()));
        return subscription;
    }
}
