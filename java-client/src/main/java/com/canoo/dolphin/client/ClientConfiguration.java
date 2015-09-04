package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;

public class ClientConfiguration {

    private String serverEndpoint;

    private boolean usePush;

    public ClientConfiguration(String serverEndpoint) {
        this(serverEndpoint, true);
    }

    public ClientConfiguration(String serverEndpoint, boolean usePush) {
        if(StringUtil.isBlank(serverEndpoint)) {
            throw new IllegalArgumentException("serverEndpoint mustn't be null");
        }
        this.serverEndpoint = serverEndpoint;
        this.usePush = usePush;
    }

    public String getServerEndpoint() {
        return serverEndpoint;
    }

    public boolean isUsePush() {
        return usePush;
    }
}
