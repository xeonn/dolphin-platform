package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ListChangeEvent;
import com.canoo.dolphin.impl.collections.ObservableArrayList;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class ObservableListWithGcSupport<E> extends ObservableArrayList<E> {

    private final GarbageCollection garbageCollection;

    public ObservableListWithGcSupport(final GarbageCollection garbageCollection) {
        this.garbageCollection = garbageCollection;
    }

    protected void notifyInternalListeners(final ListChangeEvent<E> e) {
        for (ListChangeEvent.Change<? extends E> c : e.getChanges()) {
            if (c.isRemoved()) {
                for (E elem : c.getRemovedElements()) {
                    garbageCollection.onRemovedFromList(e.getSource(), elem);
                }
            } else if (c.isAdded()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    garbageCollection.onAddedToList(e.getSource(), e.getSource().get(i));
                }
            } else if (c.isReplaced()) {
                throw new RuntimeException("Not yet implemented!");
            }
        }
    }
}
