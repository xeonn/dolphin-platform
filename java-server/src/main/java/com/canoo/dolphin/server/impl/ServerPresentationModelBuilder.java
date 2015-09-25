package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.AbstractPresentationModelBuilder;
import com.canoo.dolphin.impl.PlatformConstants;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;

import java.util.ArrayList;
import java.util.List;

public class ServerPresentationModelBuilder extends AbstractPresentationModelBuilder<ServerPresentationModel> {

    private final List<Slot> slots = new ArrayList<>();
    private final ServerDolphin dolphin;

    public ServerPresentationModelBuilder(ServerDolphin dolphin) {
        this.dolphin = dolphin;
        this.slots.add(new Slot(PlatformConstants.SOURCE_SYSTEM, PlatformConstants.SOURCE_SYSTEM_SERVER));
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name) {
        slots.add(new Slot(name, null));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value) {
        slots.add(new Slot(name, value));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value, Tag tag) {
        slots.add(new Slot(name, value, null, tag));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value, String qualifier) {
        slots.add(new Slot(name, value, qualifier));
        return this;
    }

    @Override
    public ServerPresentationModelBuilder withAttribute(String name, Object value, String qualifier, Tag tag) {
        slots.add(new Slot(name, value, qualifier, tag));
        return this;
    }

    @Override
    public ServerPresentationModel create() {
        return dolphin.presentationModel(id, type, new DTO(slots));
    }

}
