package com.canoo.dolphin.client.javafx.impl.numeric;

import javafx.beans.property.IntegerProperty;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class IntegerJavaFXBidirectionalBindable extends AbstractNumericJavaFXBidirectionalBindable<Integer> {

    public IntegerJavaFXBidirectionalBindable(final IntegerProperty javaFxProperty) {
        super(javaFxProperty);
    }

    @Override
    public Integer convertNumber(Number value) {
        if (value == null) {
            return null;
        }
        return value.intValue();
    }
}
