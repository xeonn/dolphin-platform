package com.canoo.dolphin.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMax;

public class DecimalMaxPropertyValidatorForNumber extends AbstractPropertyValidator<DecimalMax, Number> {

    private double maxValue;

    private boolean inclusive;

    public DecimalMaxPropertyValidatorForNumber() {
        super(Number.class);
    }

    public void initialize(DecimalMax maxValue) {
        try {
            this.maxValue = Double.parseDouble(maxValue.value());
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("TODO");
        }
        this.inclusive = maxValue.inclusive();
    }

    @Override
    protected boolean checkValid(Number value, ConstraintValidatorContext context) {
        if (inclusive) {
            return value.doubleValue() <= maxValue;
        } else {
            return value.doubleValue() < maxValue;
        }
    }

}

