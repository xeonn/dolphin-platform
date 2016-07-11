package com.canoo.dolphin.server.binding;

import com.canoo.dolphin.binding.Binding;
import com.canoo.dolphin.mapping.Property;

public interface PropertyBinder {

    <T> Binding bind(Property<T> property, Qualifier<T> qualifier);

}
