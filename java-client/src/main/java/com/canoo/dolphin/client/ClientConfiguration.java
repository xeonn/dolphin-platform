package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;
import org.opendolphin.core.client.comm.UiThreadHandler;

public class ClientConfiguration {

    private String serverEndpoint;

    private UiThreadHandler uiThreadHandler;

    public ClientConfiguration(String serverEndpoint, UiThreadHandler uiThreadHandler) {
        if (StringUtil.isBlank(serverEndpoint)) {
            throw new IllegalArgumentException("serverEndpoint mustn't be null");
        }
        if (uiThreadHandler == null) {
            throw new IllegalArgumentException("uiThreadHandler mustn't be null");
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
