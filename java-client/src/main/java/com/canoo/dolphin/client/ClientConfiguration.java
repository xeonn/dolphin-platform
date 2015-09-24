package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;
import org.opendolphin.core.client.comm.UiThreadHandler;

public class ClientConfiguration {

    private final String serverEndpoint;

    private final UiThreadHandler uiThreadHandler;

    public ClientConfiguration(String serverEndpoint, UiThreadHandler uiThreadHandler) {
        if (StringUtil.isBlank(serverEndpoint)) {
            throw new IllegalArgumentException("serverEndpoint must not be null");
        }
        if (uiThreadHandler == null) {
            throw new IllegalArgumentException("uiThreadHandler must not be null");
        }
        this.serverEndpoint = serverEndpoint;

        this.uiThreadHandler = uiThreadHandler;
    }

    public UiThreadHandler getUiThreadHandler() {
        return uiThreadHandler;
    }

    public String getServerEndpoint() {
        return serverEndpoint;
    }
}
