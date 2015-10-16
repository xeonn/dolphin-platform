package com.canoo.dolphin.client;

import org.opendolphin.StringUtil;
import org.opendolphin.core.client.comm.UiThreadHandler;

/**
 * Configuration class for a Dolphin Platform client. A configuration is needed to create a {@link ClientContext} by
 * using the {@link ClientContextFactory} (see {@link ClientContextFactory#connect(ClientConfiguration)}).
 * The configuration wraps the url to the Dolphin Platform server endpoint and a specific ui thread handler.
 * Since Dolphin Platform manages UI releated concurrency for you it needs a handler to call methods directly on the
 * ui thread. For platforms like JavaFX the JavaFX client lib of Dolphin Platform contains a specific
 * configuration class that extends the {@link ClientConfiguration} and already defines the needed ui handler. If
 * you want to use Dolphin Platform with a different Java based UI you need to extends this class or create a ui handler
 * on your own.
 */
public class ClientConfiguration {

    private final String serverEndpoint;

    private final UiThreadHandler uiThreadHandler;

    /**
     * Default constructor of a client configuration
     * @param serverEndpoint the DOlphin Platform server url
     * @param uiThreadHandler the ui thread handler
     */
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

    /**
     * Returns the ui thread handler
     * @return ui thread handler
     */
    public UiThreadHandler getUiThreadHandler() {
        return uiThreadHandler;
    }

    /**
     * Returns the Dolphin Platform server endpoint
     * @return the server endpoint
     */
    public String getServerEndpoint() {
        return serverEndpoint;
    }
}
