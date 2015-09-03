package com.canoo.dolphin.client;

public class ClientConfiguration {

    private String serverEndpoint;

    private boolean usePush;

    public ClientConfiguration(String serverEndpoint) {
        this(serverEndpoint, true);
    }

    public ClientConfiguration(String serverEndpoint, boolean usePush) {
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
