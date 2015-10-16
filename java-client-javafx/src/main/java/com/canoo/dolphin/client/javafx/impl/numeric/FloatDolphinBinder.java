package com.canoo.dolphin.client.javafx.impl.numeric;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class FloatDolphinBinder extends AbstractNumericDolphinBinder<Float> {

    private final static double EPSILON = 1e-10f;

    public FloatDolphinBinder(final Property<Float> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Float aFloat) {
        if (n == null && aFloat != null) {
            return false;
        }
        if (n != null && aFloat == null) {
            return false;
        }
        if (n == null && aFloat == null) {
            return true;
        }
        return Math.abs(n.floatValue() - aFloat.floatValue()) < EPSILON;
    }

    @Override
    protected BidirectionalConverter<Number, Float> getConverter() {
        return new BidirectionalConverter<Number, Float>() {
            @Override
            public Number convertBack(Float value) {
                if (value == null) {
                    return 0.0f;
                }
                return value;
            }

            @Override
            public Float convert(Number value) {
                if (value == null) {
                    return 0.0f;
                }
                return value.floatValue();
            }
        };
    }

}

