package com.canoo.dolphin.client.javafx;

import javafx.beans.value.ObservableValue;

/**
 * Created by hendrikebbers on 28.09.15.
 */
public interface NumericDolphinBindable<T extends Number> extends DolphinBindable<T> {

    Binding toNumeric(final ObservableValue<Number> observableValue);

    Binding bidirectionalToNumeric(final javafx.beans.property.Property<Number> property);

}
