package com.canoo.dolphin.impl;

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

import java.util.UUID;

public abstract class PresentationModelBuilder<T extends PresentationModel> {

    protected String type;

    protected String id;

    public PresentationModelBuilder() {
        this.id = UUID.randomUUID().toString();
    }

    public PresentationModelBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public PresentationModelBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public abstract PresentationModelBuilder withAttribute(String name);

    public abstract PresentationModelBuilder withAttribute(String name, Object value);

    public abstract PresentationModelBuilder withAttribute(String name, Object value, Tag tag);

    public abstract PresentationModelBuilder withAttribute(String name, Object value, String qualifier);

    public abstract PresentationModelBuilder withAttribute(String name, Object value, String qualifier, Tag tag);

    public abstract T create();

}
