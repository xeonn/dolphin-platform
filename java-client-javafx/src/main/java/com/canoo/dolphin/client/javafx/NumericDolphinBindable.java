package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.javafx.DolphinBindable;
import com.canoo.dolphin.event.Subscription;
import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 28.09.15.
 */
public interface NumericDolphinBindable<T extends Number> extends DolphinBindable<T> {

    Subscription toNumeric(final ObservableValue<Number> observableValue);

    Subscription bidirectionalToNumeric(final javafx.beans.property.Property<Number> property);

}
