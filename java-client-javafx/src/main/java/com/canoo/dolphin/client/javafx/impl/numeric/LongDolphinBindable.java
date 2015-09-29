package com.canoo.dolphin.client.javafx.impl.numeric;

import com.canoo.dolphin.client.javafx.BidirectionalConverter;
import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 29.09.15.
 */
public class LongDolphinBindable extends AbstractNumericDolphinBindable<Long> {

    public LongDolphinBindable(final Property<Long> property) {
        super(property);
    }

    @Override
    protected boolean equals(Number n, Long aLong) {
        if (n == null && aLong != null) {
            return false;
        }
        if (n != null && aLong == null) {
            return false;
        }
        if (n == null && aLong == null) {
            return true;
        }
        return  n.longValue() - aLong.longValue() == 0l;
    }

    @Override
    protected BidirectionalConverter<Number, Long> getConverter() {
        return new BidirectionalConverter<Number, Long>() {
            @Override
            public Number convertBack(Long value) {
                if (value == null) {
                    return 0l;
                }
                return value;
            }

            @Override
            public Long convert(Number value) {
                if (value == null) {
                    return 0l;
                }
                return value.longValue();
            }
        };
    }

}

