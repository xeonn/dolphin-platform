package com.canoo.dolphin.client.v2;

import com.canoo.dolphin.Constants;
import com.canoo.dolphin.client.ClientConfiguration;
import com.canoo.dolphin.client.impl.ClientContextImpl;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.comm.JsonCodec;

/**
 * Created by hendrikebbers on 14.09.15.
 */
public class ClientContextFactory {

    public static ClientContext connect(ClientConfiguration clientConfiguration) {
        try {
            final ClientDolphin dolphin = new ClientDolphin();
            dolphin.setClientModelStore(new ClientModelStore(dolphin));
            final HttpClientConnector clientConnector = new HttpClientConnector(dolphin, clientConfiguration.getServerEndpoint());
            clientConnector.setCodec(new JsonCodec());
            clientConnector.setUiThreadHandler(clientConfiguration.getUiThreadHandler());
            dolphin.setClientConnector(clientConnector);

            ClientContext clientContext = new ClientContextImpl(dolphin);

            dolphin.startPushListening(Constants.POLL_COMMAND_NAME, Constants.RELEASE_COMMAND_NAME);
            return clientContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
