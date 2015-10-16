package com.canoo.dolphin.client.javafx.impl.numeric;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class IntegerDolphinBinder extends AbstractNumericDolphinBinder<Integer> {

    public IntegerDolphinBinder(final Property<Integer> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Integer aInteger) {
        if (n == null && aInteger != null) {
            return false;
        }
        if (n != null && aInteger == null) {
            return false;
        }
        if (n == null && aInteger == null) {
            return true;
        }
        return  n.intValue() - aInteger.intValue() == 0;
    }

    @Override
    protected BidirectionalConverter<Number, Integer> getConverter() {
        return new BidirectionalConverter<Number, Integer>() {
            @Override
            public Number convertBack(Integer value) {
                if (value == null) {
                    return 0;
                }
                return value;
            }

            @Override
            public Integer convert(Number value) {
                if (value == null) {
                    return 0;
                }
                return value.intValue();
            }
        };
    }

}


