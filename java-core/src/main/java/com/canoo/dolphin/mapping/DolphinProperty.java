package com.canoo.dolphin.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be used to define the name of a dolphin property. A dolphin property is always defined as a field
 * of a dolphin bean see {@link com.canoo.dolphin.mapping.DolphinBean}. By default this annotation isn't needed but it
 * can be used to specify the name of the underlying Open Dolphin presentation model by hand.
 *
 * Example:
 * <code>
 *     public class MyModel {
 *
 *         @DolphinProperty("model-name")
 *         private Property<String> name;
 *
 *         public Property<String> getNameProperty() {
 *             return name;
 *         }
 *     }
 *
 * </code>
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface DolphinProperty {

    /**
     * Defines the name of the underlying Open Dolphin attribute
     * @return name of the underlying Open Dolphin attribute
     */
    String value() default "";

}
