package com.canoo.dolphin.advanced;

import com.canoo.dolphin.mapping.Property;

public interface DolphinProperty<T> extends Property<T> {

    Metadata getMetadata();

    String getClassifier();

    void setClassifier(String classifier);
}
