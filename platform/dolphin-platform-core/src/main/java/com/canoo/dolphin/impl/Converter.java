package com.canoo.dolphin.impl;

public interface Converter {

    Object convertFromDolphin(Object value);

    Object convertToDolphin(Object value);

}
