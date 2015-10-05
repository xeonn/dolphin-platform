package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * A method of a Dolphin controller (see {@link com.canoo.dolphin.server.DolphinController}) that is annotated by this
 * annotation defines a Dolphin action that can be called on the client by using the controller proxy.
 * <br>
 * <center><img src="doc-files/invoke-action.png" alt="server controller action is invoked by a client view"></center>
 *</p>
 * <p>
 * Example:
 *<blockquote>
 * <pre>
 *     {@literal @}DolphinController("my-controller")
 *     public class MyController {
 *
 *         {@literal @}DolphinAction("my-action")
 *         private void doSomeAction() { . . . };
 *     }
 * </pre>
 * </blockquote>
 *</p>
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface DolphinAction {

    /**
     * Defines the name of the action
     * @return name of the action
     */
    String value() default "";
}
