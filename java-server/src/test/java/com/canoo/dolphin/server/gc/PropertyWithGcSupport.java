package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.impl.MockedProperty;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class PropertyWithGcSupport<T> extends MockedProperty<T> {

    private final GarbageCollection garbageCollection;

    public PropertyWithGcSupport(final GarbageCollection garbageCollection) {
        this.garbageCollection = garbageCollection;
    }

    @Override
    public void set(final T value) {
        final T oldValue = get();
        super.set(value);
        garbageCollection.onPropertyValueChanged(this, oldValue, value);
    }
}
