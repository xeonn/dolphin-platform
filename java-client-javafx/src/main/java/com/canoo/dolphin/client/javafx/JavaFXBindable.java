package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface JavaFXBindable<S> {

    default Subscription to(Property<? extends S> dolphinProperty) {
        if(dolphinProperty == null) {
            throw new IllegalArgumentException("dolphinProperty must not be null");
        }
        return to(dolphinProperty, n -> n);
    }

    <T> Subscription to(Property<T> dolphinProperty, Converter<T, ? extends S> converter);

}
