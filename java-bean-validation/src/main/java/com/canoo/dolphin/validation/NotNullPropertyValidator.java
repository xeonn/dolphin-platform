package com.canoo.dolphin.validation;

import com.canoo.dolphin.mapping.Property;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

public class NotNullPropertyValidator implements ConstraintValidator<NotNull, Property> {

    @Override
    public void initialize(NotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(Property value,
                           ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.get() == null) {
            return false;
        }
        return true;
    }

}

