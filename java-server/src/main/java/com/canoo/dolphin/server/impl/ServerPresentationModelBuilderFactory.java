package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.AbstractPresentationModelBuilder;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;

public class ServerPresentationModelBuilderFactory implements PresentationModelBuilderFactory<ServerPresentationModel> {

    private final ServerDolphin dolphin;

    public ServerPresentationModelBuilderFactory(ServerDolphin dolphin) {
        this.dolphin = dolphin;
    }

    @Override
    public PresentationModelBuilder createBuilder() {
        return new ServerPresentationModelBuilder(dolphin);
    }
}
