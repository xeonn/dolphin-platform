package com.canoo.dolphin.icos.poc.platform;

import org.opendolphin.core.Tag;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PresentationModelBuilder {

    private final List<Slot> slots;

    private String type;

    private String id;

    private final ServerDolphin dolphin;

    public PresentationModelBuilder(ServerDolphin dolphin) {
        slots = new ArrayList<>();
        this.dolphin = dolphin;
        id = UUID.randomUUID().toString();
    }

    public PresentationModelBuilder withAttribute(String name) {
        slots.add(new Slot(name, null));
        return this;
    }

    public PresentationModelBuilder withAttribute(String name, Object value) {
        slots.add(new Slot(name, value));
        return this;
    }

    public PresentationModelBuilder withAttribute(String name, Object value, Tag tag) {
        slots.add(new Slot(name, value, null, tag));
        return this;
    }

    public PresentationModelBuilder withAttribute(String name, Object value, String qualifier) {
        slots.add(new Slot(name, value, qualifier));
        return this;
    }

    public PresentationModelBuilder withAttribute(String name, Object value, String qualifier, Tag tag) {
        slots.add(new Slot(name, value, qualifier, tag));
        return this;
    }

    public PresentationModelBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public PresentationModelBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ServerPresentationModel create() {
        return dolphin.presentationModel(id, type, new DTO(slots));
    }

}
