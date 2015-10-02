package com.canoo.dolphin.internal;

import com.canoo.dolphin.impl.EventDispatcherImpl;
import org.opendolphin.core.ModelStoreListener;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface EventDispatcher extends ModelStoreListener {

    void addAddedHandler(DolphinEventHandler handler);

    void addRemovedHandler(DolphinEventHandler handler);

    void addListElementAddHandler(DolphinEventHandler handler);

    void addListElementDelHandler(DolphinEventHandler handler);

    void addListElementSetHandler(DolphinEventHandler handler);
}
