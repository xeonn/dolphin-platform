package com.canoo.dolphin.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;

public class DecimalMinPropertyValidatorForNumber extends AbstractPropertyValidator<DecimalMin, Number> {

    private double minValue;

    private boolean inclusive;

    public DecimalMinPropertyValidatorForNumber() {
        super(Number.class);
    }

    public void initialize(DecimalMin minValue) {
        try {
            this.minValue = Double.parseDouble(minValue.value());
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("TODO");
        }
        this.inclusive = minValue.inclusive();
    }

    @Override
    protected boolean checkValid(Number value, ConstraintValidatorContext context) {
        if (inclusive) {
            return value.doubleValue() >= minValue;
        } else {
            return value.doubleValue() > minValue;
        }
    }

}


