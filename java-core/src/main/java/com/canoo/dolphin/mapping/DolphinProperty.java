/**
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * <strong>This feature was never used and will be removed in the next version of the Dolphin Platform</strong>
 *
 *@Deprecated
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
