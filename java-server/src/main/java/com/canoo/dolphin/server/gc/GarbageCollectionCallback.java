package com.canoo.dolphin.server.gc;

import java.util.Set;

public interface GarbageCollectionCallback {

    void onRemove(Set<Instance> instances);

}
