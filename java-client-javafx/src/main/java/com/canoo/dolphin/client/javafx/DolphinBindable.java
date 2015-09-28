package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.event.Subscription;
import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface DolphinBindable<S> {

    default Subscription to(final ObservableValue<S> observableValue){
        if(observableValue == null) {
            throw new IllegalArgumentException("observableValue must not be null");
        }
        return to(observableValue, n -> n);
    }

    <T> Subscription to(final ObservableValue<T> observableValue, final Converter<? super T, ? extends S> converter);

    default Subscription bidirectionalTo(final javafx.beans.property.Property<S> property) {
        if(property == null) {
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

    <T> Subscription bidirectionalTo(final javafx.beans.property.Property<T> property, final BidirectionalConverter<T, S> converter);
}
