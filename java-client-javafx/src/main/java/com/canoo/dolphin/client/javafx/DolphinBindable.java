package com.canoo.dolphin.client.javafx;

import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface DolphinBindable<S> {

    default Binding to(final ObservableValue<S> observableValue) {
        if (observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        return to(observableValue, n -> n);
    }

    <T> Binding to(final ObservableValue<T> observableValue, final Converter<? super T, ? extends S> converter);

    default Binding bidirectionalTo(final javafx.beans.property.Property<S> property) {
        if (property == null) {
            throw new IllegalArgumentException("javaFxProperty must not be null");
        }
        return bidirectionalTo(property, new BidirectionalConverter<S, S>() {
            @Override
            public S convertBack(S value) {
                return value;
            }

            @Override
            public S convert(S value) {
                return value;
            }
        });
    }

    <T> Binding bidirectionalTo(final javafx.beans.property.Property<T> property, final BidirectionalConverter<T, S> converter);
}
