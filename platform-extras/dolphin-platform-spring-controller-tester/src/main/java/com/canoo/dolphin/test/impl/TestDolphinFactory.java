package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.server.context.OpenDolphinFactory;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.server.DefaultServerDolphin;

/**
 * Created by hendrikebbers on 05.02.16.
 */
public class TestDolphinFactory implements OpenDolphinFactory {

    private DefaultInMemoryConfig inMemoryConfig;

    public TestDolphinFactory(DefaultInMemoryConfig inMemoryConfig) {
        this.inMemoryConfig = Assert.requireNonNull(inMemoryConfig, "inMemoryConfig");
    }

    @Override
    public DefaultServerDolphin create() {
        return inMemoryConfig.getServerDolphin();
    }
}
