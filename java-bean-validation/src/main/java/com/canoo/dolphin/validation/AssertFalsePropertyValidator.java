package com.canoo.dolphin.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertFalse;

public class AssertFalsePropertyValidator extends AbstractPropertyValidator<AssertFalse, Boolean> {

    public AssertFalsePropertyValidator() {
        super(Boolean.class);
    }

    @Override
    protected boolean checkValid(Boolean property, ConstraintValidatorContext context) {
        return !property;
    }

}

