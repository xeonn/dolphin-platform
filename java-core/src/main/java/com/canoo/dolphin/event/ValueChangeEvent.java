package com.canoo.dolphin.event;

import com.canoo.dolphin.mapping.Property;

public interface ValueChangeEvent<T> {

    Property<T> getSource();

    T getOldValue();

    T getNewValue();
}
