package com.canoo.dolphin.server.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom scope that is bound to the lifecycle of a {@link com.canoo.dolphin.server.DolphinSession}
 */
@Qualifier
@Scope(ClientScope.CLIENT_SCOPE)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientScoped {

}
