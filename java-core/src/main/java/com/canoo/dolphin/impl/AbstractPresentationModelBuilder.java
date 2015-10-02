package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

import java.util.UUID;

public abstract class AbstractPresentationModelBuilder<T extends PresentationModel> implements PresentationModelBuilder<T> {

    protected String type;

    protected String id;

    public AbstractPresentationModelBuilder() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public PresentationModelBuilder withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public PresentationModelBuilder withId(String id) {
        this.id = id;
        return this;
    }
}
