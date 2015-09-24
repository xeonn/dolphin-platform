package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hendrikebbers on 14.09.15.
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface DolphinModel {
}
