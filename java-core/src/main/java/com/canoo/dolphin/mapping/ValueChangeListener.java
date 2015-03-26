package com.canoo.dolphin.mapping;

import java.beans.PropertyChangeEvent;
import java.util.EventListener;

/**
 * Created by hendrikebbers on 23.03.15.
 */
public interface ValueChangeListener<T> extends EventListener {

    void valueChanged(ValueChangeEvent<? extends T> evt);

}
