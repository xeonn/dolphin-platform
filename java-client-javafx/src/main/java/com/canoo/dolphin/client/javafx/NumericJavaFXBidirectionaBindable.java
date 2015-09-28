package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;
import javafx.beans.value.ChangeListener;

/**
 * Created by hendrikebbers on 28.09.15.
 */
public interface NumericJavaFXBidirectionaBindable<S extends Number> extends JavaFXBidirectionalBindable<Number> {

    default Subscription bidirectionalToNumeric(Property<S> dolphinProperty) {
        return bidirectionalTo(dolphinProperty, new BidirectionalConverter<S, Number>() {
            @Override
            public S convertBack(Number value) {
                return convertNumber(value);
            }

            @Override
            public Number convert(S value) {
                return value;
            }
        });
    }

    S convertNumber(Number value);

    <T> Subscription bidirectionalToNumeric(final Property<T> property, BidirectionalConverter<T, S> converter);
}
