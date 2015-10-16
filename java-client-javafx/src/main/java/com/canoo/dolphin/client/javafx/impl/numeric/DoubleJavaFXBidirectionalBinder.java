package com.canoo.dolphin.client.javafx.impl.numeric;

import javafx.beans.property.DoubleProperty;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public class DoubleJavaFXBidirectionalBinder extends AbstractNumericJavaFXBidirectionalBinder<Double> {

    public DoubleJavaFXBidirectionalBinder(final DoubleProperty javaFxProperty) {
        super(javaFxProperty);
    }

    @Override
    public Double convertNumber(Number value) {
        if (value == null) {
            return null;
        }
        return value.doubleValue();
    }

}
