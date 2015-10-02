package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.AbstractPresentationModelBuilder;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

public class ClientPresentationModelBuilderFactory implements PresentationModelBuilderFactory<ClientPresentationModel> {

    private final ClientDolphin dolphin;

    public ClientPresentationModelBuilderFactory(ClientDolphin dolphin) {
        this.dolphin = dolphin;
    }

    @Override
    public PresentationModelBuilder createBuilder() {
        return new ClientPresentationModelBuilder(dolphin);
    }
}
