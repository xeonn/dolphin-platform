package com.canoo.dolphin.server.impl.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hendrikebbers on 17.02.16.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface AnnotationForClasspathScanTest {
}
