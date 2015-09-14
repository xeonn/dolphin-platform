package com.canoo.dolphin.client;

import javafx.application.Platform;
import org.opendolphin.StringUtil;
import org.opendolphin.core.client.comm.UiThreadHandler;

public class ClientConfiguration {

    private String serverEndpoint;

    private boolean usePush;

    private UiThreadHandler uiThreadHandler;

    public ClientConfiguration(String serverEndpoint) {
        this(serverEndpoint, true);
    }

    public ClientConfiguration(String serverEndpoint, boolean usePush) {
        if (StringUtil.isBlank(serverEndpoint)) {
            throw new IllegalArgumentException("serverEndpoint mustn't be null");
        }
        this.serverEndpoint = serverEndpoint;
        this.usePush = usePush;
        this.uiThreadHandler = r -> Platform.runLater(r);
    }

    public UiThreadHandler getUiThreadHandler() {
        return uiThreadHandler;
    }

    public String getServerEndpoint() {
        return serverEndpoint;
    }

    public boolean isUsePush() {
        return usePush;
    }
}
