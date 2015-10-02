package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * When defining a controller method by using {@link com.canoo.dolphin.server.DolphinAction} the
 * Dolphin Platform supports methods parameters. The parameters can be set on the client when calling
 * the action method. To define parameters o the server controller method each paramter must be annotated
 * with the {@link com.canoo.dolphin.server.Param} annotation.
 *</p>
 * <p>
 * Example:
 *
 * <blockquote>
 * <pre>
 *     {@literal @}DolphinController("my-controller")
 *     public class MyController {
 *
 *         {@literal @}DolphinAction("my-action")
 *         private void showById(@Param("id") id) { . . . };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String value() default "";

}
