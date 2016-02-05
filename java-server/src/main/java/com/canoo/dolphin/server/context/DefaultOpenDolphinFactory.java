package com.canoo.dolphin.server.context;

import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerModelStore;

/**
 * Created by hendrikebbers on 05.02.16.
 */
public class DefaultOpenDolphinFactory implements OpenDolphinFactory {

    @Override
    public DefaultServerDolphin create() {
        //Init Open Dolphin
        final ServerModelStore modelStore = new ServerModelStore();
        final ServerConnector serverConnector = new ServerConnector();
        serverConnector.setCodec(new JsonCodec());
        serverConnector.setServerModelStore(modelStore);
        DefaultServerDolphin dolphin = new DefaultServerDolphin(modelStore, serverConnector);
        dolphin.registerDefaultActions();
        return dolphin;
    }
}
