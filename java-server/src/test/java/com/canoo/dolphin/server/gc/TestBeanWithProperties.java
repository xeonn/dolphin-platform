package com.canoo.dolphin.server.gc;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class TestBeanWithProperties {

    private Property<String> stringProperty;

    private Property<Boolean> booleanProperty;

    private Property<Double> doubleProperty;

    private Property<TestBeanWithProperties> beanProperty;

    private Property<TestBeanWithLists> listBeanProperty;

    public TestBeanWithProperties(GarbageCollection garbageCollection) {
        this.stringProperty = new PropertyWithGcSupport<>(garbageCollection);
        this.booleanProperty = new PropertyWithGcSupport<>(garbageCollection);
        this.doubleProperty = new PropertyWithGcSupport<>(garbageCollection);
        this.beanProperty = new PropertyWithGcSupport<>(garbageCollection);
        this.listBeanProperty = new PropertyWithGcSupport<>(garbageCollection);
    }

    public Property<String> stringProperty() {
        return stringProperty;
    }

    public Property<Boolean> booleanProperty() {
        return booleanProperty;
    }

    public Property<Double> doubleProperty() {
        return doubleProperty;
    }

    public Property<TestBeanWithProperties> beanProperty() {
        return beanProperty;
    }

    public Property<TestBeanWithLists> listBeanProperty() {
        return listBeanProperty;
    }
}
