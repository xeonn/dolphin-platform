package com.canoo.dolphin.client.javafx.impl.numeric;

import javafx.beans.property.LongProperty;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class LongJavaFXBidirectionalBindable extends AbstractNumericJavaFXBidirectionalBindable<Long> {

    public LongJavaFXBidirectionalBindable(final LongProperty javaFxProperty) {
        super(javaFxProperty);
    }

    @Override
    public Long convertNumber(Number value) {
        if (value == null) {
            return null;
        }
        return value.longValue();
    }
}
