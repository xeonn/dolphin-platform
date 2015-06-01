package com.canoo.dolphin.client.util;

import com.canoo.dolphin.collections.ObservableList;

public class ListReferenceModel {

    public enum DataType {LIST_VALUE_1, LIST_VALUE_2}

    private ObservableList<SimpleTestModel> objectList;

    private ObservableList<String> primitiveList;

    public ObservableList<SimpleTestModel> getObjectList() {
        return objectList;
    }

    public ObservableList<String> getPrimitiveList() {
        return primitiveList;
    }
}
