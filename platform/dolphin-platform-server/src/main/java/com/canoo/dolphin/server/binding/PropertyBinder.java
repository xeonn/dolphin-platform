package com.canoo.dolphin.server.binding;

import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.mapping.Property;

/**
 * A component that can be use to create bindings between properties (see {@link Property}). All properties that are
 * bound to the same qualifier (see {@link Qualifier}) will be automatically updated once one of the properties change
 * its value.
 */
public interface PropertyBinder {

    /**
     * Method to bind a property to a qualifier
     * @param property the property
     * @param qualifier the qualifier
     * @param <T> generic type of the property
     * @return a binding that can be used to unbind the property
     */
    <T> Binding bind(Property<T> property, Qualifier<T> qualifier);

}
