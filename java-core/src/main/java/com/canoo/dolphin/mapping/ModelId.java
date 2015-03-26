package com.canoo.dolphin.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hendrikebbers on 19.03.15.
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelId {
}
