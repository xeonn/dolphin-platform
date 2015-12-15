package com.canoo.dolphin.validation;

import org.testng.annotations.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class ValidationTest {

    @Test
    public void testValidators() {
        TestBean bean = new TestBean();

        Configuration<?> validationConf = Validation.byDefaultProvider().configure();
        Validator validator = validationConf.buildValidatorFactory().getValidator();

        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        ConstraintViolation<TestBean> violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value1");

        bean.value1Property().set("YEAH!");

        violations = validator.validate(bean);
        assertEquals(violations.size(), 0);

        bean.value2Property().set("TEST");

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value2");

        bean.value2Property().set(null);

        bean.value3Property().set(false);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value3");

        bean.value3Property().set(Boolean.FALSE);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value3");

        bean.value3Property().set(true);

        bean.value4Property().set(true);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value4");

        bean.value4Property().set(Boolean.TRUE);

        violations = validator.validate(bean);
        assertEquals(violations.size(), 1);
        violation = violations.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "value4");
    }

}
