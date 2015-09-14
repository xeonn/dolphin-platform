package com.canoo.dolphin.advanced;

import com.canoo.dolphin.event.Subscription;
import com.canoo.dolphin.event.ValueChangeListener;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public interface Metadata {

    Subscription onChanged(String name, ValueChangeListener<KeyValuePair> listener);

    void set(String name, Object value);

    <T> T get(String name);
}
