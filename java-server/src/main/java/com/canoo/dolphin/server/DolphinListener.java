package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark a listener.
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface DolphinListener {
}
