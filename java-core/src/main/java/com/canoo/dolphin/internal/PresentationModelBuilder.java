package com.canoo.dolphin.internal;

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface PresentationModelBuilder<T extends PresentationModel> {

    PresentationModelBuilder<T> withType(String type);

    PresentationModelBuilder<T> withId(String id);

    PresentationModelBuilder withAttribute(String name);

    PresentationModelBuilder withAttribute(String name, Object value);

    PresentationModelBuilder withAttribute(String name, Object value, Tag tag);

    PresentationModelBuilder withAttribute(String name, Object value, String qualifier);

    PresentationModelBuilder withAttribute(String name, Object value, String qualifier, Tag tag);

    T create();
}
