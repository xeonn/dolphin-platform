package com.canoo.dolphin.client.javafx.impl.numeric;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 27.09.15.
 */
public class DoubleDolphinBinder extends AbstractNumericDolphinBinder<Double> {

    private final static double EPSILON = 1e-10;

    public DoubleDolphinBinder(final Property<Double> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Double aDouble) {
        if(n == null && aDouble != null) {
            return false;
        }
        if(n != null && aDouble == null) {
            return false;
        }
        if(n == null && aDouble == null) {
            return true;
        }
        return Math.abs(n.doubleValue() - aDouble.doubleValue()) < EPSILON;
    }

    @Override
    protected BidirectionalConverter<Number, Double> getConverter() {
        return new BidirectionalConverter<Number, Double>() {
            @Override
            public Number convertBack(Double value) {
                if(value == null) {
                    return 0.0;
                }
                return value;
            }

            @Override
            public Double convert(Number value) {
                if(value == null) {
                    return 0.0;
                }
                return value.doubleValue();
            }
        };
    }

}
