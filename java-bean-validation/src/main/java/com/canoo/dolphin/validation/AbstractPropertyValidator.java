package com.canoo.dolphin.validation;

import com.canoo.dolphin.mapping.Property;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public abstract class AbstractPropertyValidator<T extends Annotation, U> implements ConstraintValidator<T, Property> {

    private Class<U> supportedValueClass;

    public AbstractPropertyValidator(Class<U> supportedValueClass) {
        this.supportedValueClass = supportedValueClass;
    }

    @Override
    public void initialize(T constraintAnnotation) {
    }

    protected abstract boolean checkValid(U value,
                           ConstraintValidatorContext context);

    protected boolean onNullValue() {
        return true;
    }

    @Override
    public boolean isValid(Property property,
                           ConstraintValidatorContext context) {
        if (property == null) {
            return onNullValue();
        }

        Object value = property.get();

        if(value == null) {
            return onNullValue();
        }
        if(supportedValueClass.isAssignableFrom(value.getClass()) ||
                supportedValueClass.equals(Boolean.class) && value.getClass().equals(Boolean.TYPE)) {
            return checkValid((U) value, context);
        }


        throw new RuntimeException("Property contains value of type " + value.getClass() + " instead of " + supportedValueClass);
    }

}

