package com.canoo.dolphin.server.util;

import com.canoo.dolphin.impl.ReflectionHelper;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;

import java.util.ArrayList;

/**
 * Created by hendrikebbers on 30.03.15.
 */
public abstract class AbstractDolphinBasedTest {

    protected ServerDolphin createServerDolphin() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig();
        config.getServerDolphin().registerDefaultActions();

        ServerModelStore store = config.getServerDolphin().getServerModelStore();
        try {
            ReflectionHelper.setPrivileged(ServerModelStore.class.getDeclaredField("currentResponse"), store, new ArrayList<>());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return config.getServerDolphin();
    }
}
