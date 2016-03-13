/**
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
