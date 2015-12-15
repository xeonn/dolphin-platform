package com.canoo.dolphin.validation;

import com.canoo.dolphin.mapping.Property;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Null;

public class NullPropertyValidator implements ConstraintValidator<Null, Property> {

    @Override
    public void initialize(Null constraintAnnotation) {
    }

    @Override
    public boolean isValid(Property value,
                           ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.get() == null) {
            return true;
        }
        return false;
    }

}

