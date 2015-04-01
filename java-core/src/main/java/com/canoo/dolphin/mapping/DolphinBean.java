package com.canoo.dolphin.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be used to define the type of a dolphin bean. By default the definition of a bean don't need any
 * annotation but if you want to specify the presentation model type of the underlying Open Dolphin presentation model
 * you can use this annotation.
 *
 * Example:
 * <code>
 *     @DolphinBean("my-data-model")
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
@Target(ElementType.TYPE)
public @interface DolphinBean {

    /**
     * Defines the type of the underlying Open Dolphin presentation model
     * @return type of the underlying Open Dolphin presentation model
     */
    String value() default "";

}
