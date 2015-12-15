package com.canoo.dolphin.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertTrue;

public class AssertTruePropertyValidator extends AbstractPropertyValidator<AssertTrue, Boolean> {

    public AssertTruePropertyValidator() {
        super(Boolean.class);
    }

    @Override
    protected boolean checkValid(Boolean property, ConstraintValidatorContext context) {
        return property;
    }

}

