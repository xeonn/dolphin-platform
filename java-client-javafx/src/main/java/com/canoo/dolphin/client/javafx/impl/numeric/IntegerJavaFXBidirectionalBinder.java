package com.canoo.dolphin.client.javafx.impl.numeric;

import javafx.beans.property.IntegerProperty;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class IntegerJavaFXBidirectionalBinder extends AbstractNumericJavaFXBidirectionalBinder<Integer> {

    public IntegerJavaFXBidirectionalBinder(final IntegerProperty javaFxProperty) {
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
