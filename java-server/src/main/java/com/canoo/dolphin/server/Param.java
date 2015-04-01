package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hendrikebbers on 31.03.15.
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value() default "";

}
