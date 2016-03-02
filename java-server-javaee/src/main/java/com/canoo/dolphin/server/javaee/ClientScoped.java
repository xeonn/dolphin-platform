package com.canoo.dolphin.server.javaee;

import javax.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom scope that is bound to the lifecycle of a {@link com.canoo.dolphin.server.DolphinSession}
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
public @interface ClientScoped {
}
