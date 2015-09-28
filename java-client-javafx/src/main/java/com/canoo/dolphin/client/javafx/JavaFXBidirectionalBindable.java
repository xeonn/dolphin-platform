package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface JavaFXBidirectionalBindable<S> {

    Subscription bidirectionalTo(Property<S> dolphinProperty);

    <T> Subscription bidirectionalTo(final Property<T> property, BidirectionalConverter<T, S> converter);

}
