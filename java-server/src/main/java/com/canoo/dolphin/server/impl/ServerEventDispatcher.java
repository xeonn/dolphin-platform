package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.DolphinConstants;
import com.canoo.dolphin.impl.EventDispatcherImpl;
import org.opendolphin.core.Dolphin;

public class ServerEventDispatcher extends EventDispatcherImpl {

    public ServerEventDispatcher(Dolphin dolphin) {
        super(dolphin);
    }

    @Override
    protected String getLocalSystemIdentifier() {
        return DolphinConstants.SOURCE_SYSTEM_SERVER;
    }
}
