package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ObservableList;

public class ListReference extends Reference {

    private ObservableList list;

    public ListReference(Instance parent, ObservableList list, Instance child) {
        super(parent, child);
        this.list = list;
    }

    public ObservableList getList() {
        return list;
    }
}
