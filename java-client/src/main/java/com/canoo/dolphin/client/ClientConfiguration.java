package com.canoo.dolphin.client;

public class ClientConfiguration {

    private String serverEndpoint;

    private boolean usePush;

    public ClientConfiguration(String serverEndpoint) {
        this(serverEndpoint, true);
    }

    public ClientConfiguration(String serverEndpoint, boolean usePush) {
        if(serverEndpoint == null) {
            throw new IllegalArgumentException("serverEndpoint can't be null");
        }
        if(serverEndpoint.length() == 0) {
            throw new IllegalArgumentException("serverEndpoint can't be empty");
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
