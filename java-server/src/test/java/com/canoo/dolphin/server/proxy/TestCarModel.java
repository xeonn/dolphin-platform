package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.mapping.Property;

import java.io.Serializable;
import java.util.List;


public interface TestCarModel extends Serializable, Comparable {

    Integer getYear();
    void setYear(Integer inYear);

    TestCarManufacturer getCarManufacturer();
    void setCarManufacturer(TestCarManufacturer manufacturer);

    public Property<String> getBrandNameProperty();

    List<Integer> getTripKilometerCounters();

    List<TestCarColor> getCarColors();

}
