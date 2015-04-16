package com.canoo.dolphin.server.proxy;

import com.canoo.dolphin.mapping.Property;

/**
 * Created by hendrikebbers on 15.04.15.
 */

public interface TestCarModel {



    Integer getYear();
    void setYear(Integer inYear);

    public Property<String> getBrandNameProperty();



}
