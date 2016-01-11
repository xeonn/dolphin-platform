package com.canoo.dolphin.client.javafx;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ClientContextFactory;
import com.canoo.dolphin.client.ClientInitializationException;
import com.canoo.dolphin.client.ClientShutdownException;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class DolphinPlatformApplication extends Application {

    private ClientContext clientContext;

    private ClientInitializationException initializationException;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            clientContext = ClientContextFactory.connect(getClientConfiguration()).get();
        } catch (Exception e) {
            initializationException = new ClientInitializationException("Can't initialize Dolphin Platform Context", e);
        }
    }

    protected JavaFXConfiguration getClientConfiguration() {
        return new JavaFXConfiguration(getServerEndpoint());
    }

    protected abstract String getServerEndpoint();

    @Override
    public final void start(Stage primaryStage) throws Exception {
        if (initializationException == null) {
            if(clientContext != null) {
                start(primaryStage, clientContext);
            } else {
                onInitializationError(primaryStage, new ClientInitializationException("No clientContext was created!"));
            }
        } else {
            onInitializationError(primaryStage, initializationException);
        }
    }

    protected abstract void start(Stage primaryStage, ClientContext clientContext) throws Exception;

    public void onInitializationError(Stage primaryStage, ClientInitializationException initializationException) throws Exception {
        initializationException.printStackTrace();
        System.exit(-1);
    }

    @Override
    public final void stop() throws Exception {
        if(clientContext != null) {
            try {
                clientContext.disconnect().get();
                onShutdown();
            } catch (Exception e) {
                onShutdownException(new ClientShutdownException(e));
            }
        }
    }

    protected void onShutdownException(ClientShutdownException shutdownException) {
        shutdownException.printStackTrace();
        System.exit(-1);
    }

    protected void onShutdown() {
        System.exit(0);
    }

}
