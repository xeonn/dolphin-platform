package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.collections.ObservableList;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class TestBeanWithLists {

    private ObservableList<String> stringList;

    private ObservableList<Boolean> booleanList;

    private ObservableList<Double> doubleList;

    private ObservableList<TestBeanWithLists> beansList;

    private ObservableList<TestBeanWithProperties> beansList2;

    public TestBeanWithLists(GarbageCollection garbageCollection) {
        stringList = new ObservableListWithGcSupport(garbageCollection);
        booleanList = new ObservableListWithGcSupport(garbageCollection);
        doubleList = new ObservableListWithGcSupport(garbageCollection);
        beansList = new ObservableListWithGcSupport(garbageCollection);
        beansList2 = new ObservableListWithGcSupport(garbageCollection);
    }

    public ObservableList<String> getStringList() {
        return stringList;
    }

    public ObservableList<Boolean> getBooleanList() {
        return booleanList;
    }

    public ObservableList<Double> getDoubleList() {
        return doubleList;
    }

    public ObservableList<TestBeanWithLists> getBeansList() {
        return beansList;
    }

    public ObservableList<TestBeanWithProperties> getBeansList2() {
        return beansList2;
    }
}
