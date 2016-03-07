package com.canoo.dolphin.server.event.impl;

import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextProvider;

/**
 * Created by hendrikebbers on 11.03.16.
 */
public class DolphinEventBusImplMock extends DolphinEventBusImpl {

    private final String dolphinId;

    public DolphinEventBusImplMock(String dolphinId) {
        super(new DolphinContextProvider() {
            @Override
            public DolphinContext getCurrentContext() {
                return null;
            }
        });
       this.dolphinId = dolphinId;
    }

    @Override
    protected String getDolphinId() {
        return dolphinId;
    }
}
