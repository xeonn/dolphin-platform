package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public interface JavaFXBinder<S> {

    default Binding to(Property<? extends S> dolphinProperty) {
        if (dolphinProperty == null) {
            throw new IllegalArgumentException("dolphinProperty must not be null");
        }
        return to(dolphinProperty, n -> n);
    }

    <T> Binding to(Property<T> dolphinProperty, Converter<? super T, ? extends S> converter);

}
