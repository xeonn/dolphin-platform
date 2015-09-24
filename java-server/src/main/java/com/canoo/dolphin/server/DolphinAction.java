package com.canoo.dolphin.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A method of a Dolphin controller (see {@link com.canoo.dolphin.server.DolphinController}) that is annotated by this
 * annotation defines a Dolphin action that can be called on the client.
 * Each dolpin action is defined by a unique id that is created by the controller name and the action name. Let's say
 * the controller is defined by the name "my-controller" and the action is defined by "my-action". In this case the
 * unique command name of the action is "my-controller:my-action". The : char is allways used as a separator between the
 * controller name and the action name.
 *
 * Example:
 *
 *  <code>
 *     @DolphinController("my-controller")
 *     public class MyController {
 *
 *         @DolphinAction("my-action")
 *         private void doSomeAction() { . . . };
 *     }
 * </code>
 *
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
