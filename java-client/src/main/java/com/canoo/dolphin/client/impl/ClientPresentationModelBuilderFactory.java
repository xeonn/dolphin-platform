package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.impl.PresentationModelBuilder;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import org.opendolphin.core.client.ClientDolphin;

public class ClientPresentationModelBuilderFactory implements PresentationModelBuilderFactory {

    private final ClientDolphin dolphin;

    public ClientPresentationModelBuilderFactory(ClientDolphin dolphin) {
        this.dolphin = dolphin;
    }

    @Override
    public PresentationModelBuilder createBuilder() {
        return new ClientPresentationModelBuilder(dolphin);
    }
}
