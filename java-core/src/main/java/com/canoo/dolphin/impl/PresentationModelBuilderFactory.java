package com.canoo.dolphin.impl;

import com.canoo.dolphin.internal.PresentationModelBuilder;
import org.opendolphin.core.PresentationModel;

public interface PresentationModelBuilderFactory<T extends PresentationModel> {

    PresentationModelBuilder<T> createBuilder();

}
