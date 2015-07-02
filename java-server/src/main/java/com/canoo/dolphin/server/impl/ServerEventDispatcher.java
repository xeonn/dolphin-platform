package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.DolphinConstants;
import com.canoo.dolphin.impl.EventDispatcher;
import org.opendolphin.core.Dolphin;

public class ServerEventDispatcher extends EventDispatcher {

    public ServerEventDispatcher(Dolphin dolphin) {
        super(dolphin);
    }

    @Override
    protected String getLocalSystemIdentifier() {
        return DolphinConstants.SOURCE_SYSTEM_SERVER;
    }
}
