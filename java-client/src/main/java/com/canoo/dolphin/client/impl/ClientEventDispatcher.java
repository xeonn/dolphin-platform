package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.DolphinConstants;
import com.canoo.dolphin.impl.EventDispatcher;
import org.opendolphin.core.Dolphin;

public class ClientEventDispatcher extends EventDispatcher {

    public ClientEventDispatcher(Dolphin dolphin) {
        super(dolphin);
    }

    @Override
    protected String getLocalSystemIdentifier() {
        return DolphinConstants.SOURCE_SYSTEM_CLIENT;
    }
}
