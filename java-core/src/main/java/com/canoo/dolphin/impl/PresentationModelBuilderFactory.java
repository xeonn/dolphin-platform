package com.canoo.dolphin.impl;

import org.opendolphin.core.PresentationModel;

public interface PresentationModelBuilderFactory<T extends PresentationModel> {

    PresentationModelBuilder<T> createBuilder();

}
