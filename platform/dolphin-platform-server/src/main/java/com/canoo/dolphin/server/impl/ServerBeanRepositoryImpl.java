package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.server.impl.gc.GarbageCollection;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Dolphin;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public class ServerBeanRepositoryImpl extends BeanRepositoryImpl implements ServerBeanRepository{

    final GarbageCollection garbageCollection;

    public ServerBeanRepositoryImpl(final Dolphin dolphin, final EventDispatcher dispatcher, final GarbageCollection garbageCollection) {
        super(dolphin, dispatcher);
        this.garbageCollection = Assert.requireNonNull(garbageCollection, "garbageCollection");
    }

    @Override
    public <T> void delete(T bean) {
        super.delete(bean);
        garbageCollection.onBeanRemoved(bean);
    }

    @Override
    public <T> void deleteByGC(T bean) {
        super.delete(bean);
    }
}
